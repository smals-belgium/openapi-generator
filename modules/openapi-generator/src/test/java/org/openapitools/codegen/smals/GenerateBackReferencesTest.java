package org.openapitools.codegen.smals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.openapitools.codegen.java.assertions.JavaFileAssert;
import org.openapitools.codegen.smals.utils.SmalsCodegenTestUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.openapitools.codegen.languages.AbstractJavaCodegen.BACK_REFERENCES;
import static org.openapitools.codegen.smals.utils.SmalsCodegenTestUtils.generateFromContract;

public class GenerateBackReferencesTest {

    @ParameterizedTest
    @EnumSource(value = SmalsCodegenTestUtils.Generator.class)
    public void shouldGenerateJacksonReferenceAnnotationsWhenBackReferencesEnabled(SmalsCodegenTestUtils.Generator generator) throws IOException {
        Map<String, File> files = generateFromContract(
                "src/test/resources/3_0/smals/generateBackReferences.yaml",
                generator,
                Map.of(BACK_REFERENCES, true),
                codegen -> codegen.addOpenapiNormalizer("REF_AS_PARENT_IN_ALLOF", "true"));

        JavaFileAssert fileAssert = JavaFileAssert.assertThat(files.get("MyCollection.java"))
                .hasImports("com.fasterxml.jackson.annotation.JsonBackReference", "com.fasterxml.jackson.annotation.JsonManagedReference");

        assertAnnotated(fileAssert, "getParent", "JsonBackReference");
        assertAnnotated(fileAssert, "setParent", "JsonBackReference");

        assertAnnotated(fileAssert, "getObjects", "JsonManagedReference");
        assertAnnotated(fileAssert, "setObjects", "JsonManagedReference");

        assertAnnotated(fileAssert, "getSubProperty", "JsonManagedReference");
        assertAnnotated(fileAssert, "setSubProperty", "JsonManagedReference");

    }

    private void assertAnnotated(
            JavaFileAssert fileAssert,
            String methodName,
            String annotation) {

        fileAssert.assertMethod(methodName)
                .assertMethodAnnotations()
                .containsWithName(annotation);
    }
}

