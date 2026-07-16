package org.openapitools.codegen.smals;

import org.junit.jupiter.api.Test;
import org.openapitools.codegen.java.assertions.JavaFileAssert;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.openapitools.codegen.smals.utils.SmalsCodegenTestUtils.Generator.JAXRS_DEFAULT;
import static org.openapitools.codegen.smals.utils.SmalsCodegenTestUtils.generateFromContract;

public class GenerateWithMultipartFormDataTest {

    @Test
    public void generatesMultipartFormDataInputWhenUsingMultipartFormStyleResteasy() throws IOException {
        Map<String, File> files = generateFromContract(
                "src/test/resources/3_0/smals/generateWithMultipartFormData.yaml",
                JAXRS_DEFAULT,
                Map.of("multipartFormStyle", "resteasy", "interfaceOnly", true),
                codegen -> codegen.addOpenapiNormalizer("REF_AS_PARENT_IN_ALLOF", "true"));

        JavaFileAssert.assertThat(files.get("PetApi.java"))
                .hasImports("org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput")
                .assertMethod("uploadFile")
                .assertParameter("multipartFormDataInput")
                .hasType("MultipartFormDataInput");
    }

    @Test
    public void generatesCustomPojoAnnotatedWithMultipartFormWhenUsingMultipartFormStyleResteasyPojo() throws IOException {
        Map<String, File> files = generateFromContract(
                "src/test/resources/3_0/smals/generateWithMultipartFormData.yaml",
                JAXRS_DEFAULT,
                Map.of("multipartFormStyle", "resteasy-pojo"),
                codegen -> codegen
                        .addOpenapiNormalizer("REF_AS_PARENT_IN_ALLOF", "true")
                        .addGlobalProperty("skipFormModel", "false")); // enable generation of form object

        JavaFileAssert.assertThat(files.get("PetApi.java"))
                .hasImports("org.jboss.resteasy.annotations.providers.multipart.MultipartForm")
                .assertMethod("uploadFile")
                .assertParameter("uploadImageForm")
                .hasType("UploadImageForm")
                .assertParameterAnnotations()
                .containsWithName("MultipartForm");
        JavaFileAssert.assertThat(files.get("UploadImageForm.java"));
    }
}