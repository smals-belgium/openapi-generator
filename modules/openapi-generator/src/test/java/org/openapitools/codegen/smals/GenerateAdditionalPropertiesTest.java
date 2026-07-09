package org.openapitools.codegen.smals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.openapitools.codegen.java.assertions.JavaFileAssert;
import org.openapitools.codegen.smals.utils.SmalsCodegenTestUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static org.openapitools.codegen.smals.utils.SmalsCodegenTestUtils.generateFromContract;

public class GenerateAdditionalPropertiesTest {

    @DisplayName("Generate additional properties when additionalProperties is set to true in a schema")
    @ParameterizedTest
    @EnumSource(value = SmalsCodegenTestUtils.Generator.class)
    public void generateAdditionalProperties(SmalsCodegenTestUtils.Generator generator) throws IOException {
        Map<String, File> files = generateFromContract(
                "src/test/resources/3_0/smals/generateAdditionalProperties.yaml",
                generator,
                Collections.emptyMap(),
                codegen -> codegen.addOpenapiNormalizer("REF_AS_PARENT_IN_ALLOF", "true")); // Added to validate generation with inheritance

        JavaFileAssert.assertThat(files.get("Person.java"))
                .isNormalClass()
                .assertProperty("additionalProperties")
                .withType("Map<String, Object>")
                .toType()
                .assertMethod("putAdditionalProperty").assertMethodAnnotations().containsWithName("JsonAnySetter").toMethod().toFileAssert()
                .assertMethod("getAdditionalProperties").assertMethodAnnotations().containsWithName("JsonAnyGetter").toMethod().toFileAssert()
                .printFileContent();

        JavaFileAssert.assertThat(files.get("Child.java"))
                .extendsClass("Parent")
                .assertProperty("additionalProperties")
                .withType("Map<String, Object>")
                .toType()
                .assertMethod("putAdditionalProperty").assertMethodAnnotations().containsWithName("JsonAnySetter").toMethod().toFileAssert()
                .assertMethod("getAdditionalProperties").assertMethodAnnotations().containsWithName("JsonAnyGetter").toMethod().toFileAssert()
                .printFileContent();
        ;

    }
}

