package org.openapitools.codegen.smals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.openapitools.codegen.java.assertions.JavaFileAssert;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.openapitools.codegen.smals.utils.SmalsCodegenTestUtils.Generator;
import static org.openapitools.codegen.smals.utils.SmalsCodegenTestUtils.generateFromContract;

// Added test because generating a builder with inheritance is not tested upstream
public class GenerateBuildersWithInheritanceTest {

    @ParameterizedTest()
    @EnumSource(Generator.class)
    public void generateBuildersWithInheritanceTest(Generator generator) throws IOException {

        Map<String, File> files = generateFromContract(
                "src/test/resources/3_0/smals/generateBuildersWithInheritance.yaml",
                generator,
                Map.of("generateBuilders", "true"),
                codegen -> codegen.addOpenapiNormalizer("REF_AS_PARENT_IN_ALLOF", "true"));

        if ("spring".equals(generator.getGeneratorName()) || "java".equals(generator.getGeneratorName())) {
            JavaFileAssert.assertThat(files.get("Child.java"))
                    .extendsClass("Parent")
                    .assertInnerClass("Builder")
                    .toFileAssert()
                    .fileContains("Builder extends Parent.Builder");
        }

        if ("jaxrs-spec".equals(generator.getGeneratorName())) {
            JavaFileAssert.assertThat(files.get("Child.java"))
                    .extendsClass("Parent")
                    .assertInnerClass("ChildBuilder")
                    .toFileAssert()
                    .fileContains("ChildBuilder<C extends Child, B extends ChildBuilder<C, B>> extends ParentBuilder<C, B>");
        }
    }
}
