package org.openapitools.codegen.smals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.openapitools.codegen.java.assertions.JavaFileAssert;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static org.openapitools.codegen.smals.utils.SmalsCodegenTestUtils.Generator;
import static org.openapitools.codegen.smals.utils.SmalsCodegenTestUtils.generateFromContract;


public class UseFirstRefAsParentTest {

    @ParameterizedTest()
    @EnumSource(Generator.class)
    public void shouldInheritFromFirstRefInAllOfTest(Generator generator) throws IOException {

        Map<String, File> files = generateFromContract(
                "src/test/resources/3_0/smals/first-ref-as-parent/firstRefAsParentInAllOf.yaml",
                generator,
                Collections.emptyMap(),
                codegen -> codegen.addOpenapiNormalizer("REF_AS_PARENT_IN_ALLOF", "true"));

        JavaFileAssert.assertThat(files.get("ChildInternalParentSchema.java"))
                .extendsClass("Parent");

        JavaFileAssert.assertThat(files.get("ChildExternalParentSchema.java"))
                .extendsClass("ExternalParent");
    }

    @ParameterizedTest()
    @EnumSource(value = Generator.class)
    public void shouldNotInheritFromFirstRefInOneOfTest(Generator generator) throws IOException {

        Map<String, File> files = generateFromContract(
                "src/test/resources/3_0/smals/first-ref-as-parent/firstRefNotParentInOneOf.yaml",
                generator,
                Collections.emptyMap(),
                codegen -> codegen.addOpenapiNormalizer("REF_AS_PARENT_IN_ALLOF", "true"));

        JavaFileAssert.assertThat(files.get("ShouldNotBeAChild.java"))
                .doesNotExtendsClasses();
    }

    @ParameterizedTest()
    @EnumSource(value = Generator.class)
    public void shouldNotInheritFromFirstRefInAnyOfTest(Generator generator) throws IOException {

        Map<String, File> files = generateFromContract(
                "src/test/resources/3_0/smals/first-ref-as-parent/firstRefNotParentInAnyOf.yaml",
                generator,
                Collections.emptyMap(),
                codegen -> codegen.addOpenapiNormalizer("REF_AS_PARENT_IN_ALLOF", "true"));

        JavaFileAssert.assertThat(files.get("ShouldNotBeAChild.java"))
                .printFileContent()
                .doesNotExtendsClasses();
        JavaFileAssert.assertThat(files.get("Type.java"))
                .printFileContent()
                .doesNotExtendsClasses();
    }

}
