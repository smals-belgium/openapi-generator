package org.openapitools.codegen.smals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openapitools.codegen.java.assertions.JavaFileAssert;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;


public class UseFirstRefAsParentTest extends AbstractSmalsCodegenTest {


    @ParameterizedTest()
    @MethodSource("generators")
    public void getParentFromFirstRefInAllOfTest(GeneratorName generatorName, String library) throws IOException {

        Map<String, File> files = generateFromContract(
                "src/test/resources/3_0/smals/first-ref-as-parent/firstRefAsParent.yaml",
                generatorName,
                library,
                Collections.emptyMap(),
                codegen -> codegen.addOpenapiNormalizer("REF_AS_PARENT_IN_ALLOF", "true"));

        JavaFileAssert.assertThat(files.get("ChildInternalParentSchema.java"))
                .extendsClass("Parent");

        JavaFileAssert.assertThat(files.get("ChildExternalParentSchema.java"))
                .extendsClass("ExternalParent");
    }

}
