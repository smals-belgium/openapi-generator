package org.openapitools.codegen.smals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.openapitools.codegen.java.assertions.JavaFileAssert;
import org.openapitools.codegen.smals.utils.SmalsCodegenTestUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.openapitools.codegen.smals.utils.SmalsCodegenTestUtils.generateFromContract;

public class GenerateXFieldExtraAnnotationTest {


    private static Map<String, File> getFiles(SmalsCodegenTestUtils.Generator generator) throws IOException {
        return generateFromContract(
                "src/test/resources/3_0/smals/generateXFieldExtraAnnotation.yaml",
                generator,
                Map.of("useBeanValidation", "true"));
    }

    @ParameterizedTest
    @EnumSource(value = SmalsCodegenTestUtils.Generator.class, names = {"SPRING_BOOT", "JAXRS_DEFAULT"})
    public void shouldGenerateOperationParameterAnnotationForXFieldExtraAnnotation(SmalsCodegenTestUtils.Generator generator) throws IOException {
        Map<String, File> files = getFiles(generator);

        JavaFileAssert.assertThat(files.get("PetsApi.java"))
                .assertMethod("getPet")
                .assertParameter("petId")
                .assertParameterAnnotations().containsWithName("be.belgium.gcloud.rest.petstore.annotation.PetId");
    }

    @ParameterizedTest
    @EnumSource(value = SmalsCodegenTestUtils.Generator.class, names = {"SPRING_BOOT", "JAXRS_DEFAULT"})
    public void shouldGenerateFieldAnnotationForXFieldExtraAnnotation(SmalsCodegenTestUtils.Generator generator) throws IOException {
        Map<String, File> files = getFiles(generator);

        JavaFileAssert.assertThat(files.get("Pet.java"))
                .printFileContent()
                .assertProperty("petId")
                .assertPropertyAnnotations().containsWithName("be.belgium.gcloud.rest.petstore.annotation.PetId").toProperty().toType()
                .assertMethod("getOwners").hasReturnType("List<@be.belgium.gcloud.rest.petstore.annotation.OwnerId String>");
    }
}

