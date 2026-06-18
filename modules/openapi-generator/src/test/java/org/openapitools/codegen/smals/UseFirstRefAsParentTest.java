package org.openapitools.codegen.smals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openapitools.codegen.java.assertions.JavaFileAssert;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static org.openapitools.codegen.languages.AbstractJavaCodegen.GENERATE_BUILDERS;


public class UseFirstRefAsParentTest extends AbstractSmalsCodegenTest {


    @ParameterizedTest()
    @MethodSource("generatorConfigurations")
    public void getParentFromFirstRefInAllOfTest(GeneratorName generatorName, String library) throws IOException {

        Map<String, File> files = generateFromContract(
                "src/test/resources/3_0/smals/first-ref-as-parent/firstRefAsParent.yaml",
                generatorName,
                library,
                Collections.emptyMap(),
                codegen -> codegen.addOpenapiNormalizer("REF_AS_PARENT_IN_ALLOF", "true"));

        JavaFileAssert.assertThat(files.get("ChildOne.java"))
                .extendsClass("Parent");

        JavaFileAssert.assertThat(files.get("ChildTwo.java"))
                .extendsClass("ExternalParent");
    }

    @ParameterizedTest()
    @MethodSource("generatorConfigurations")
    public void getParentFromFirstRefInAllOfWithBuilderTest(GeneratorName generatorName, String library) throws IOException {

        Map<String, File> files = generateFromContract(
                "src/test/resources/3_0/smals/first-ref-as-parent/firstRefAsParentWithBuilder.yaml",
                generatorName,
                library,
                Map.of(GENERATE_BUILDERS, true),
                codegen -> {
                    codegen.addOpenapiNormalizer("REF_AS_PARENT_IN_ALLOF", "true");
                    codegen.addSchemaMapping("SomeEnumObject","org.openapitools.codegen.smals.SomeEnumObject");
                });

        JavaFileAssert.assertThat(files.get("ChildOne.java"))
                .printFileContent()
                .extendsClass("Parent");

        JavaFileAssert.assertThat(files.get("ChildTwo.java"))

                .extendsClass("org.openapitools.codegen.smals.SomeEnumObject");
    }

}
