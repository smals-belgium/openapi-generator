package org.openapitools.codegen.smals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.openapitools.codegen.java.assertions.JavaFileAssert;
import org.openapitools.codegen.java.assertions.MethodAssert;
import org.openapitools.codegen.smals.utils.SmalsCodegenTestUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.openapitools.codegen.smals.utils.SmalsCodegenTestUtils.generateFromContract;

public class GenerateXExtraAnnotationsTest {

    public static final String OPENAPI = "src/test/resources/3_0/smals/generateXExtraAnnotation.yaml";

    private static Map<String, File> getFiles(SmalsCodegenTestUtils.Generator generator) throws IOException {
        return generateFromContract(
                OPENAPI,
                generator,
                Map.of("useBeanValidation", "true"));
    }

    @ParameterizedTest
    @EnumSource(value = SmalsCodegenTestUtils.Generator.class, names = {"SPRING_BOOT", "JAXRS_DEFAULT"})
    public void shouldGenerateAnnotationsForOperationParameters(SmalsCodegenTestUtils.Generator generator) throws IOException {
        Map<String, File> files = getFiles(generator);

        MethodAssert getPet = JavaFileAssert.assertThat(files.get("PetsApi.java"))
                .assertMethod("getPet");
        // x-extra-annotation on header parameter
        assertParameterHasAnnotation(getPet, "headerParam", "be.belgium.gcloud.rest.annotation.HeaderParam");

        // x-extra-annotation on query parameter
        assertParameterHasAnnotation(getPet, "queryParam", "be.belgium.gcloud.rest.annotation.QueryParam");

        // x-extra-annotation on path parameter
        assertParameterHasAnnotation(getPet, "pathParam", "be.belgium.gcloud.rest.annotation.PathParam");

        MethodAssert addPet = JavaFileAssert.assertThat(files.get("PetsApi.java"))
                .assertMethod("addPet");

        // x-extra-annotation on form parameter
        assertParameterHasAnnotation(addPet, "id", "be.belgium.gcloud.rest.annotation.FormParam");
    }

    @ParameterizedTest
    @EnumSource(value = SmalsCodegenTestUtils.Generator.class, names = {"SPRING_BOOT", "JAXRS_DEFAULT"})
    public void shouldGenerateAnnotationsOnPojoGetters(SmalsCodegenTestUtils.Generator generator) throws IOException {
        Map<String, File> files = getFiles(generator);

        JavaFileAssert.assertThat(files.get("Pet.java")).printFileContent()
                // x-extra-annotation on a schema property
                .assertMethod("getPetId")
                .assertMethodAnnotations()
                .containsWithName("be.belgium.gcloud.rest.annotation.PetId").toMethod().toFileAssert()

                // Multiple x-extra-annotations on a schema property
                .assertMethod("getName")
                .assertMethodAnnotations()
                .containsWithName("be.belgium.gcloud.rest.annotation.ValidName")
                .containsWithName("be.belgium.gcloud.rest.annotation.ValidChars").toMethod().toFileAssert()

                // x-extra-annotation on an array item type
                .assertMethod("getOwners").hasReturnType("List<@be.belgium.gcloud.rest.annotation.OwnerId String>");
    }

    private void assertParameterHasAnnotation(MethodAssert methodAssert, String parameterName, String annotationName) {
        methodAssert.assertParameter(parameterName).assertParameterAnnotations().containsWithName(annotationName);
    }

}

