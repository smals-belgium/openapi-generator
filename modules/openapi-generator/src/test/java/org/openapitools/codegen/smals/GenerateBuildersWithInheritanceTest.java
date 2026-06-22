package org.openapitools.codegen.smals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openapitools.codegen.java.assertions.JavaFileAssert;

import java.io.File;
import java.io.IOException;
import java.util.Map;


public class GenerateBuildersWithInheritanceTest extends AbstractSmalsCodegenTest {

    @ParameterizedTest()
    @MethodSource("generators")
    public void generateBuildersWithInheritanceTest(GeneratorName generatorName, String library) throws IOException {

        Map<String, File> files = generateFromContract(
                "src/test/resources/3_0/smals/generateBuildersWithInheritance.yaml",
                generatorName,
                library,
                Map.of("generateBuilders", "true"),
                codegen -> codegen.addOpenapiNormalizer("REF_AS_PARENT_IN_ALLOF", "true"));

        if (GeneratorName.SPRING.equals(generatorName) || GeneratorName.JAVA.equals(generatorName)) {
            JavaFileAssert.assertThat(files.get("Child.java"))
                    .extendsClass("Parent")
                    .printFileContent()
                    .assertInnerClass("Builder")
                    .toFileAssert()
                    .fileContains("Builder extends Parent.Builder");
        }

        if (GeneratorName.JAXRS.equals(generatorName)) {
            JavaFileAssert.assertThat(files.get("Child.java"))
                    .extendsClass("Parent")
                    .printFileContent()
                    .assertInnerClass("ChildBuilder")
                    .toFileAssert()
                    .fileContains("ChildBuilder<C extends Child, B extends ChildBuilder<C, B>> extends ParentBuilder<C, B>");
        }
    }
}
