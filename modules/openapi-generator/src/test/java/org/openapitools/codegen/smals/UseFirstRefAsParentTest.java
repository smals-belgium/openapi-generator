package org.openapitools.codegen.smals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.openapitools.codegen.java.assertions.JavaFileAssert;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static org.openapitools.codegen.smals.utils.SmalsCodegenUtils.Generator;
import static org.openapitools.codegen.smals.utils.SmalsCodegenUtils.generateFromContract;


public class UseFirstRefAsParentTest  {

    @ParameterizedTest()
    @EnumSource(Generator.class)
    public void getParentFromFirstRefInAllOfTest(Generator generator) throws IOException {

        Map<String, File> files = generateFromContract(
                "src/test/resources/3_0/smals/first-ref-as-parent/firstRefAsParent.yaml",
                generator,
                Collections.emptyMap(),
                codegen -> codegen.addOpenapiNormalizer("REF_AS_PARENT_IN_ALLOF", "true"));

        JavaFileAssert.assertThat(files.get("ChildInternalParentSchema.java"))
                .extendsClass("Parent");

        JavaFileAssert.assertThat(files.get("ChildExternalParentSchema.java"))
                .extendsClass("ExternalParent");
    }

}
