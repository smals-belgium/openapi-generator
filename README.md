# smals-belgium fork of OpenAPI Generator

This is a fork of the OpenAPI Generator project with changes to the `jaxrs-spec` (server), `spring` (server/client) and `java` (client) code generators in the
`openapi-generator` project. The goal of this fork is to improve the default code generation and provide features and fixes that are not yet available in the upstream OpenAPI Generator project.

The following generators contain Smals-specific changes:

The documentation below describes the features that this fork adds, and the recommended `pom.xml` build configuration
to generate server or client code from an OpenAPI specification.

**Table of contents**

[[_TOC_]]

## OpenAPI Generator extensions

### Additional configuration options

| generator             | Option                | Default    | Description                                                                                                                                                                                                                                                                           |                                                                                           
|-----------------------|-----------------------|------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| jaxrs-spec            | multipartFormStyle    | `spec`     | Adds platform-specific multipart support to the generated code (`spec` is default behavior, `resteasy` injects the `org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput` parameter, `resteasy-pojo` injects a custom POJO annotated with `@MultipartForm` parameter |
| java / jaxrs / spring | backReferences        | `false`    | Whether to generate @JsonBackReference and @JsonManagedReference annotations                                                                                                                                                                                                          |


### OpenAPI extensions
>[!NOTE]
>This part needs to be updated.
>The use of adding bean validation with extensions and how to add them is not yet decided.
>Using x-extra-annotation does clash with upstream logic, that is using the same vendor extension.

* It allows to add a custom JSR-303 Bean validation annotation to the generated code (using the vendor
  extension `x-extra-annotation`)

```yaml
  parameters:
    - name: "enterpriseNumber"
      in: "path"
      description: "Identifier issued by CBE for a registered organization"
      required: true
      schema:
        $ref: "./belgif/organization/identifier/v1/organization-identifier-v1.yaml#/components/schemas/EnterpriseNumber"
      x-extra-annotation: '@be.belgium.gcloud.validation.EnterpriseNumber'
```

### Bug fixes

#### Spring

- generate `EnumConverterConfiguration.java` also with `interfaceOnly=true`
- removes "application/problem" from generated Spring annotations as response media type, so JSON won't be used
  unexpectedly for non-error response

## Code generation behavior

### Additional properties

In contrast to previous versions (< 7.0), by default, the recommended openapi-generator configuration does not generate
getter or setter methods for any additional properties undefined in the OpenAPI document. This recommended configuration
is listed in the pom.xml extracts further below and is also applied to all clients generated from an apidef project.

Instead, the preferred approach is to explicitly specify `additionalProperties: true` (or with a specific schema) within
the schema object in the OpenAPI file. This allows for more granular control over when undefined properties are parsed
and corresponding methods (`@JsonAnyGetter`, `@JsonAnySetter`) are generated.

Example:

```yaml
MyObject:
  description: example object with additional properties.
  additionalProperties: true # additional properties of any type
```

Example with a specific schema:

```yaml
MyObject:
  description: example object with additional properties.
  additionalProperties:
    type: string # all additional properties have to be a string
```

### Parent class

A generated Java class can only extend one other class. The recommended openapi-generator configuration will prioritize
the first value in the allOf array, if it is a `$ref`. This recommended configuration is listed in the pom.xml extracts
further below and is also applied to all clients generated from an apidef project.
Changing the order of the allOf entries will change the parent class.

Note: from openapi-generator 7.15 and higher, this behavior changed in the open source version.

Example:

```yaml
MyObject:
  description: The generated java class of this class will extend from Parent. The properties from OtherObject will be copied into this object.
  allOf:
    - $ref: /components/schemas/Parent
    - $ref: /components/schemas/OtherObject

Parent:
  description: This will be the parent class
  type: object
  properties:
    name:
      type: string

OtherObject:
  description: The properties of this object will be copied into MyObject
  type: object
  properties:
    age:
      type: int
```
## Configuration

### Compatibility

#### Spring Boot 4 compatibility
- From version 7.20.0 SB4 is supported for the spring generator.
- For clients only http-interfaces are supported
- Replace rest-jackson-spring-starter dependency by [rest-jackson3-defaults-spring-boot-starter](https://git-gcd.gcloud.belgium.be/rest/jackson3-defaults#rest-jackson3-defaults-spring-boot-starter) to apply recommended (de)serialization config
- For JsonNullable, make sure to use at least version 0.2.11, which supports Jackson3.
- known issue: don't set useSpringBoot4 or useJackson3. These settings aren't compatible with JsonNullable, and the generated code will work on Spring Boot 4 even without; jackson v2 and v3 have the same annotation classes. This issue will be solved in future plugin versions > 7.20

```xml
<dependency>
    <groupId>io.github.smals-belgium</groupId>
    <artifactId>jackson-databind-nullable</artifactId>
    <version>0.2.11</version> <!-- use latest available version -->
</dependency>
<dependency>
    <groupId>be.belgium.gcloud.rest.jackson3</groupId>
    <artifactId>rest-jackson3-defaults-spring-boot-starter</artifactId>
    <version>1.0.0</version> <!-- use latest available version -->
</dependency>
```

### Generator configuration

#### Jspecify (Java and Spring generator)

JSpecify is a standard set of Java annotations that provides explicit nullness information in source code.
It allows APIs and models to clearly express whether a value may be null or is expected to be non-null,
improving code readability and enabling static analysis tools to detect potential null-related issues before runtime.
When enabled, the generated code includes JSpecify nullness annotations, such as @Nullable.
To compile the generated code, you must add the `org.jspecify:jspecify` dependency to your project.
For more information, see the [JSpecify documentation](https://jspecify.dev/).

```xml
<dependency>
    <groupId>org.jspecify</groupId>
    <artifactId>jspecify</artifactId>
    <version>1.0.0</version>
</dependency> 
```

### pom.xml 

#### JaxRS Server generation

Add the following plugins to your maven pom.xml build file.

```xml
<plugins>
    <plugin>
        <groupId>io.github.smals-belgium</groupId>
        <artifactId>openapi-generator-maven-plugin</artifactId>
        <version>${openapi-generator.version}</version>

        <executions>
            <execution>
                <goals>
                    <goal>generate</goal>
                </goals>
                <configuration>
                    <inputSpec>${project.basedir}/src/main/resources/META-INF/openapi.yaml</inputSpec>
                    <generatorName>jaxrs-spec</generatorName>
                    <openapiNormalizer>REF_AS_PARENT_IN_ALLOF=true,REFACTOR_ALLOF_WITH_PROPERTIES_ONLY=false</openapiNormalizer>
                    <generateSupportingFiles>false</generateSupportingFiles>
                    <skipOperationExample>true</skipOperationExample> <!-- don't generate verbose example values in merged OpenAPI file -->
                    <configOptions>
                        <sourceFolder>src/main/java</sourceFolder>
                        <apiPackage>be.smals.rest.reference.service.api</apiPackage> <!-- change to Java package for your project -->
                        <modelPackage>be.smals.rest.reference.representations</modelPackage> <!-- change to Java package for your project -->
                        <interfaceOnly>true</interfaceOnly>
                        <returnResponse>true</returnResponse>
                        <useBeanValidation>true</useBeanValidation> <!-- optional, if you want Jakarta/JEE bean validation annotations on generated classes-->
                        <dateLibrary>java8</dateLibrary>
                        <useSwaggerV3Annotations>true</useSwaggerV3Annotations> <!-- enables OpenAPI 3 (Swagger v3) annotations; Swagger 2 annotations are used by default -->
                        <useOneOfInterfaces>true</useOneOfInterfaces> <!-- whether to use a java interface to describe a set of oneOf options, where each option is a class that implements the interface -->
                        <legacyDiscriminatorBehavior>false</legacyDiscriminatorBehavior> <!-- all gcloud generators have support for discriminators. With legacy config: The mapping in the discriminator includes descendent schemas that allOf inherit from self and the discriminator mapping schemas in the OAS document. -->
                        <!-- <backReferences>true</backReferences> optional, to generate @JsonBackReference and @JsonManagedReference annotations on model classes -->
                        <!-- when using multipart form payloads:
                         
                         a) with your own custom POJO model class. Maps an UploadLogoFormData type defined in the OpenAPI file to custom POJO class 
            
                        <multipartFormStyle>resteasy-pojo</multipartFormStyle>
                        <importMappings>
                          <importMapping>UploadLogoFormData=be.smals.rest.reference.forms.UploadLogoFormData</importMapping>
                        </importMappings>
                        
                        b)  using resteasy's generic class `org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput
                        <multipartFormStyle>resteasy</multipartFormStyle>
                        -->
                    </configOptions>
                    <globalProperties>
                        <skipFormModel>false</skipFormModel>
                    </globalProperties>
                </configuration>
            </execution>
        </executions>
    </plugin>

    <plugin>
        <artifactId>maven-resources-plugin</artifactId>
        <executions>
            <execution>
                <id>copy-resources</id>
                <phase>generate-resources</phase>
                <goals>
                    <goal>copy-resources</goal>
                </goals>
                <configuration>
                    <outputDirectory>${basedir}/target/classes/META-INF</outputDirectory>
                    <resources>
                        <resource>
                            <directory>${basedir}/target/generated-sources/openapi</directory>
                            <filtering>true</filtering>
                        </resource>
                    </resources>
                </configuration>
            </execution>
        </executions>
    </plugin>
</plugins>
```

#### Spring Server generation

Add the following plugins to your maven pom.xml build file.

```xml

<plugins>
    <plugin>
        <groupId>io.github.smals-belgium</groupId>
        <artifactId>openapi-generator-maven-plugin</artifactId>
        <version>${openapi-generator.version}</version>
        <executions>
            <execution>
                <goals>
                    <goal>generate</goal>
                </goals>
                <configuration>
                    <inputSpec>${project.basedir}/src/main/resources/openapi.yaml</inputSpec>
                    <generatorName>spring</generatorName>
                    <supportingFilesToGenerate>EnumConverterConfiguration.java</supportingFilesToGenerate>
                    <openapiNormalizer>REF_AS_PARENT_IN_ALLOF=true,REFACTOR_ALLOF_WITH_PROPERTIES_ONLY=false</openapiNormalizer>
                    <skipOperationExample>true</skipOperationExample> <!-- don't generate verbose example values in merged OpenAPI file -->

                    <schemaMappings> <!-- optional, recommended when using belgif-rest-problem-java https://belgif.github.io/rest-problem-java/latest/#openapi-generator-maven-plugin -->
                        Problem=io.github.belgif.rest.problem.api.Problem,
                        InputValidationProblem=io.github.belgif.rest.problem.api.InputValidationProblem,
                        InputValidationIssue=io.github.belgif.rest.problem.api.InputValidationIssue,
                        InvalidParamProblem=io.github.belgif.rest.problem.api.InputValidationProblem,
                        InvalidParam=io.github.belgif.rest.problem.api.InvalidParam
                    </schemaMappings>

                    <configOptions>
                        <unhandledException>true</unhandledException> <!-- optional, if you want a "throws Exception" on each operation -->
                        <interfaceOnly>true</interfaceOnly>
                        <useSpringController>true</useSpringController>
                        <skipDefaultInterface>true</skipDefaultInterface>
                        <apiPackage>be.belgium.gcloud.rest.reference.api</apiPackage> <!-- change to Java package for your project -->
                        <modelPackage>be.belgium.gcloud.rest.reference.model</modelPackage> <!-- change to Java package for your project -->
                        <configPackage>be.belgium.gcloud.rest.reference.configuration</configPackage> <!-- change to Java package for your project. Has generated helper classes for (de)serialization e.g. for enum types.-->
                        <useJspecify>true</useJspecify> <!-- Generate JSpecify nullability annotations -->
                        <!-- <useOneOfInterfaces>false</useOneOfInterfaces> <!-- disable the use a java interface to describe a set of oneOf options, where each option is a class that implements the interface -->
                        <!-- <backReferences>true</backReferences> <!-- optional, to generate @JsonBackReference and @JsonManagedReference annotations on model classes -->
                        <!-- <useBeanValidation>false</useBeanValidation> <!-- optional, if you want to disable generation of Jakarta/JEE bean validation annotations on classes -->
                        <!-- <generateBuilders>true</generateBuilders> <!-- optional, to generate builders on model classes -->
                        <!-- <generateConstructorWithAllArgs>true</generateConstructorWithAllArgs> <!-- optional -->
                    </configOptions>
                </configuration>
            </execution>
        </executions>
    </plugin>

    <plugin>
        <artifactId>maven-resources-plugin</artifactId>
        <executions>
            <execution>
                <id>copy-resources</id>
                <phase>generate-resources</phase>
                <goals>
                    <goal>copy-resources</goal>
                </goals>
                <configuration>
                    <outputDirectory>${basedir}/target/classes/META-INF</outputDirectory>
                    <resources>
                        <resource>
                            <directory>${basedir}/target/generated-sources/openapi</directory>
                            <filtering>true</filtering>
                        </resource>
                    </resources>
                </configuration>
            </execution>
        </executions>
    </plugin>
</plugins>
```

#### Spring http-interface Server generation
>[!NOTE]
>Document http interface server generation?
> * Enable bean validation
> * Other config needed?

#### Client generation

##### Spring Boot http-interface (recommended)

When using Spring Boot >= 3, this client type is recommended.

Add the following plugins to your maven pom.xml build file.

```xml
<plugins>
    <plugin>
        <groupId>io.github.smals-belgium</groupId>
        <artifactId>openapi-generator-maven-plugin</artifactId>
        <executions>
            <execution>
                <id>generate-http-interface</id>
                <phase>generate-resources</phase>
                <goals>
                    <goal>generate</goal>
                </goals>
                <configuration>
                    <generatorName>spring</generatorName>
                    <library>spring-http-interface</library>
                    <generateSupportingFiles>false</generateSupportingFiles>
                    <generateApiDocumentation>false</generateApiDocumentation>
                    <generateModelDocumentation>false</generateModelDocumentation>
                    <openapiNormalizer>REF_AS_PARENT_IN_ALLOF=true,REFACTOR_ALLOF_WITH_PROPERTIES_ONLY=false</openapiNormalizer>
                    <skipOperationExample>true</skipOperationExample>

                    <schemaMappings> <!-- optional, recommended when using belgif-rest-problem-java https://belgif.github.io/rest-problem-java/latest/#openapi-generator-maven-plugin -->
                        Problem=io.github.belgif.rest.problem.api.Problem,
                        InputValidationProblem=io.github.belgif.rest.problem.api.InputValidationProblem,
                        InputValidationIssue=io.github.belgif.rest.problem.api.InputValidationIssue,
                        InvalidParamProblem=io.github.belgif.rest.problem.api.InputValidationProblem,
                        InvalidParam=io.github.belgif.rest.problem.api.InvalidParam
                    </schemaMappings>

                    <configOptions>
                        <apiPackage>be.smals.services.rest.${service.name}.httpinterface.api</apiPackage> <!-- change to Java package for your project -->
                        <modelPackage>be.smals.services.rest.${service.name}.httpinterface.model</modelPackage> <!-- change to Java package for your project -->
                        <useJspecify>true</useJspecify> <!-- Generate JSpecify nullability annotations -->
                        <useBeanValidation>false</useBeanValidation> <!-- Disable bean validation client side -->
                        <!-- <useOneOfInterfaces>false</useOneOfInterfaces> <!-- disable the use a java interface to describe a set of oneOf options, where each option is a class that implements the interface -->
                        <!-- <legacyDiscriminatorBehavior>true</legacyDiscriminatorBehavior> <!-- all gcloud generators have support for discriminators. With legacy config: The mapping in the discriminator includes descendent schemas that allOf inherit from self and the discriminator mapping schemas in the OAS document. -->  
                        <!-- <backReferences>true</backReferences> <!--optional, to generate @JsonBackReference and @JsonManagedReference annotations on model classes -->
                        <!-- <generateBuilders>true</generateBuilders> <!-- optional, to generate builders on model classes -->
                    </configOptions>
                </configuration>
            </execution>
        </executions>
    </plugin>
    <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>build-helper-maven-plugin</artifactId>
        <version>3.6.1</version>
        <executions>
            <execution>
                <id>test</id>
                <phase>generate-sources</phase>
                <goals>
                    <goal>add-source</goal>
                </goals>
                <configuration>
                    <sources>
                        <source>${basedir}/target/generated-sources/openapi</source>
                    </sources>
                </configuration>
            </execution>
        </executions>

        <dependencies>
            <dependency>
                <groupId>${project.groupId}</groupId>
                <artifactId>services-rest-commons-http-interface</artifactId>
                <type>pom</type>
            </dependency>
            <dependency> <!-- optional, to use belgif-rest-problem-java library -->
                <groupId>io.github.belgif.rest.problem</groupId>
                <artifactId>belgif-rest-problem-spring-boot-3</artifactId> <!-- or artifactId belgif-rest-problem for only the problem classes without Spring REST integration -->
                <version>${rest-problem-java.version}</version>
            </dependency>
        </dependencies>
    </plugin>
    <plugin>
        <artifactId>maven-resources-plugin</artifactId>
        <executions>
            <execution>
                <id>copy-resources</id>
                <phase>generate-resources</phase>
                <goals>
                    <goal>copy-resources</goal>
                </goals>
                <configuration>
                    <outputDirectory>${basedir}/target/classes/META-INF</outputDirectory>
                    <resources>
                        <resource>
                            <directory>${basedir}/target/generated-sources/openapi</directory>
                            <filtering>true</filtering>
                        </resource>
                    </resources>
                </configuration>
            </execution>
        </executions>
    </plugin>
</plugins>
```

##### Resteasy, resttemplate and webclient

Add the following plugins to your maven pom.xml build file.

```xml
<plugins>
    <plugin>
        <groupId>io.github.smals-belgium</groupId>
        <artifactId>openapi-generator-maven-plugin</artifactId>
        <version>>${openapi-generator.version}</version>

        <executions>
            <execution>
                <goals>
                    <goal>generate</goal>
                </goals>
                <configuration>
                    <inputSpec>${project.basedir}/src/main/resources/META-INF/openapi.yaml</inputSpec>
                    <generatorName>java</generatorName>
                    <skipOperationExample>true</skipOperationExample> <!-- don't generate verbose example values in merged OpenAPI file -->
                    <library>resttemplate|webclient|resteasy</library> <!-- pick resttemplate for spring sync client, webclient for spring async client, resteasy for jboss -->
                    <generateSupportingFiles>true</generateSupportingFiles> <!-- generates helper classes required by clients (e.g. ApiClient). Set to false and set invokerPackage to use your own helper classes -->

                    <schemaMappings> <!-- optional, recommended when using belgif-rest-problem-java https://belgif.github.io/rest-problem-java/latest/#openapi-generator-maven-plugin -->
                        Problem=io.github.belgif.rest.problem.api.Problem,
                        InputValidationProblem=io.github.belgif.rest.problem.api.InputValidationProblem,
                        InputValidationIssue=io.github.belgif.rest.problem.api.InputValidationIssue,
                        InvalidParamProblem=io.github.belgif.rest.problem.api.InputValidationProblem,
                        InvalidParam=io.github.belgif.rest.problem.api.InvalidParam
                    </schemaMappings>

                    <configOptions>
                        <apiPackage>be.belgium.gcloud.rest.api</apiPackage> <!-- change to a Java package for your project -->
                        <modelPackage>be.belgium.gcloud.rest.model</modelPackage> <!-- change to a Java package for your project -->
                        <useJakartaEe>true</useJakartaEe>
                        <useOneOfInterfaces>true</useOneOfInterfaces> <!-- whether to use a java interface to describe a set of oneOf options, where each option is a class that implements the interface -->
                        <legacyDiscriminatorBehavior>false</legacyDiscriminatorBehavior> <!-- all gcloud generators have support for discriminators. With legacy config: The mapping in the discriminator includes descendent schemas that allOf inherit from self and the discriminator mapping schemas in the OAS document. -->
                        <useJspecify>true</useJspecify> <!-- Generate JSpecify nullability annotations -->
                        <!-- <backReferences>true</backReferences> <!-- optional, to generate @JsonBackReference and @JsonManagedReference annotations on model classes -->
                        <!-- <generateBuilders>true</generateBuilders> <!-- optional, to generate builders on model classes -->
                        <!-- <generateConstructorWithAllArgs>true</generateConstructorWithAllArgs> <!-- optional -->
                    </configOptions>
                </configuration>
            </execution>
        </executions>

        <dependencies>
            <dependency> <!-- optional, to use belgif-rest-problem-java library -->
                <groupId>io.github.belgif.rest.problem</groupId>
                <artifactId>belgif-rest-problem-spring-boot-3
                </artifactId> <!-- or artifactId belgif-rest-problem for only the problem classes without Spring REST integration -->
                <version>${rest-problem-java.version}</version>
            </dependency>
        </dependencies>

    </plugin>
    <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>build-helper-maven-plugin</artifactId>
        <version>3.6.1</version>
        <executions>
            <execution>
                <id>test</id>
                <phase>generate-sources</phase>
                <goals>
                    <goal>add-source</goal>
                </goals>
                <configuration>
                    <sources>
                        <source>${basedir}/target/generated-sources/openapi</source>
                    </sources>
                </configuration>
            </execution>
        </executions>
    </plugin>
    <plugin>
        <artifactId>maven-resources-plugin</artifactId>
        <executions>
            <execution>
                <id>copy-resources</id>
                <phase>generate-resources</phase>
                <goals>
                    <goal>copy-resources</goal>
                </goals>
                <configuration>
                    <outputDirectory>${basedir}/target/classes/META-INF</outputDirectory>
                    <resources>
                        <resource>
                            <directory>${basedir}/target/generated-sources/openapi</directory>
                            <filtering>true</filtering>
                        </resource>
                    </resources>
                </configuration>
            </execution>
        </executions>
    </plugin>
</plugins>
```

#### Model-only generation

If you want to generate model classes for OpenAPI documents that only define schemas, it is recommended to use the same pom.xml configuration listed in "Spring Server generation" above, as this generator is the most stable.

If your OpenAPI also contains operations for which you don't wish to generate code, you can add the option `<generateApis>false</generateApis>`within `<configuration>`.

Note that only classes for schemas in the entry OpenAPI document, or referenced from it, are generated.

## Custom Problem response schemas

When using the `belgif-rest-problem-java` library, with the pom.xml `schemaMappings` recommended configuration like above, only standard Belgif Problem response schemas from `openapi-problem` will work out-of-the-box. No classes will be generated for these Problem schemas.

If an API provides its own Problem variant extending the Belgif one, an **uncompilable** class is generated with invalid inheritance.
To remediate this, either:

* add an additional `schemaMapping` to the pom.xml: `<schemaMapping>MyCustomProblem=io.github.belgif.rest.problem.api.Problem</schemaMapping>`
    * occurrences of MyCustomProblem will then be converted to `DefaultProblem` Java class. Additional properties can be accessed using `Problem.getAdditionalProperties()`
* provide your own `MyCustomProblem` implementation as described in [belgif-rest-problem-java documentation](https://belgif.github.io/rest-problem-java/latest/#custom-problem-types), and add a `schemaMapping` to the pom.xml: `<schemaMapping>MyCustomProblem=org.example.myapplication.MyCustomProblem</schemaMapping>`

Only the first option is supported for clients generated by ICC, and has to be configured by ICC for each API with a custom Problem schema.