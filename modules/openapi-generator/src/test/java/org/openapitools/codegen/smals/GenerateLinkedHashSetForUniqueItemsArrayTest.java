package org.openapitools.codegen.smals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.openapitools.codegen.java.assertions.JavaFileAssert;
import org.openapitools.codegen.smals.utils.SmalsCodegenTestUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.openapitools.codegen.smals.utils.SmalsCodegenTestUtils.generateFromContract;

// Adapt generated Spring models by using LinkedHashSet instead of Set implementations and removing redundant @JsonDeserialize setter annotations.
public class GenerateLinkedHashSetForUniqueItemsArrayTest {

    @DisplayName("Should not generate @JsonDeserialize annotations for LinkedHashSet properties")
    @ParameterizedTest
    @EnumSource(value = SmalsCodegenTestUtils.Generator.class, names = {"SPRING_BOOT", "SPRING_HTTP_INTERFACE"})
    public void shouldGenerateLinkedHashSetForUniqueItemsArrayWithoutJsonDeserializeTest(SmalsCodegenTestUtils.Generator generator) throws IOException {
        Map<String, File> files = generateFromContract(
                "src/test/resources/3_0/smals/generateLinkedHashSetForUniqueItemsArray.yaml",
                generator,
                Map.of("useBeanValidation", false)); // Turned off bean validation for asserts, since it's not relevant for this test

        JavaFileAssert.assertThat(files.get("MyCollection.java"))
                .hasNoImports("com.fasterxml.jackson.databind.annotation.JsonDeserialize")
                .assertProperty("objects")
                .withType("LinkedHashSet<MyObject>")
                .toType()
                .assertMethod("setObjects")
                .assertParameter("objects")
                .hasType("LinkedHashSet<MyObject>")
                .toMethod()
                .assertMethodAnnotations()
                .doesNotContainWithName("JsonDeserialize");

        JavaFileAssert.assertThat(files.get("MyObjectsApi.java")).assertMethod("myObjectsGet").hasReturnType("ResponseEntity<LinkedHashSet<MyObject>>");
    }
}
