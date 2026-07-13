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


public class GenerateEnumConverterConfigurationTest {

    @ParameterizedTest(name = "{0} should generate EnumConverterConfiguration")
    @EnumSource(value = Generator.class, names = {"SPRING_BOOT", "SPRING_HTTP_INTERFACE"})
    public void shouldGenerateEnumConverterConfigurationIfOpenApiContainsEnumsTest(Generator generator) throws IOException {

        Map<String, File> files = generateFromContract(
                "src/test/resources/3_0/smals/generateEnumConverterConfiguration.yaml",
                generator,
                Collections.emptyMap(), //
                codegenConfigurator -> {});

        JavaFileAssert.assertThat(files.get("EnumConverterConfiguration.java"));
    }

    /**
     * Regression test for:
     * Generation of EnumConverterConfiguration class with `interfaceOnly=true`.
     *
     * When `interfaceOnly=true` and the generated API contains enum request parameters,
     * the Spring generators must still generate EnumConverterConfiguration so that
     * String -> Enum request parameter conversion works correctly.
     */
    @ParameterizedTest(name = "{0} should generate EnumConverterConfiguration when interfaceOnly=true")
    @EnumSource(value = Generator.class, names = {"SPRING_BOOT", "SPRING_HTTP_INTERFACE"})
    void shouldGenerateEnumConverterConfigurationForInterfaceOnlyApis(Generator generator) throws IOException {

        Map<String, File> files = generateFromContract(
                "src/test/resources/3_0/smals/generateEnumConverterConfiguration.yaml",
                generator,
                Map.of("interfaceOnly", true),
                codegenConfigurator -> {
                });

        JavaFileAssert.assertThat(files.get("EnumConverterConfiguration.java"));

    }

}

