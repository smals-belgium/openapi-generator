package org.openapitools.codegen.smals;

import lombok.Getter;
import org.junit.jupiter.params.provider.Arguments;
import org.openapitools.codegen.ClientOptInput;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.config.CodegenConfigurator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.openapitools.codegen.languages.SpringCodegen.SPRING_BOOT;
import static org.openapitools.codegen.languages.SpringCodegen.SPRING_HTTP_INTERFACE;

public class AbstractSmalsCodegenTest {
    /**
     * Generate the contract with additional configuration.
     * <p>
     * use CodegenConfigurator instead of CodegenConfig for easier configuration like in JavaClientCodeGenTest
     */
    protected Map<String, File> generateFromContract(String url, GeneratorName generatorName, String library, Map<String, Object> additionalProperties,
                                                     Consumer<CodegenConfigurator> consumer) throws IOException {

        File output = Files.createTempDirectory("test").toFile().getCanonicalFile();
        System.out.println(output.getAbsolutePath());
        output.deleteOnExit();

        final CodegenConfigurator configurator = new CodegenConfigurator()
                .setGeneratorName(generatorName.getType())
                .setAdditionalProperties(additionalProperties)
                .setValidateSpec(false)
                .setInputSpec(url)
                .setOutputDir(output.getAbsolutePath());
        if (null != library) {
            configurator.setLibrary(library);
        }
        consumer.accept(configurator);

        ClientOptInput input = configurator.toClientOptInput();
        DefaultGenerator generator = new DefaultGenerator();
        generator.setGenerateMetadata(false);

        return generator.opts(input).generate().stream()
                .collect(Collectors.toMap(this::getUniqueName, Function.identity()));
    }

    private String getUniqueName(File file) {
        String name = file.getName();
        if ("package-info.java".equals(name)) {
            return file.getParentFile().getName() + "/" + name;
        }
        return name;
    }

    @Getter
    public enum GeneratorName {
        JAVA("java"),
        JAXRS("jaxrs-spec"),
        SPRING("spring");

        private final String type;

        GeneratorName(String type) {
            this.type = type;
        }

    }

    protected static Stream<Arguments> generatorConfigurations() {
        return Stream.of(
                Arguments.of(GeneratorName.SPRING, SPRING_HTTP_INTERFACE),
                Arguments.of(GeneratorName.SPRING, SPRING_BOOT),
                Arguments.of(GeneratorName.JAVA, "restclient"),
                Arguments.of(GeneratorName.JAVA, "resteasy"),
                Arguments.of(GeneratorName.JAVA, "resttemplate"),
                Arguments.of(GeneratorName.JAVA, "webclient"),
                Arguments.of(GeneratorName.JAXRS, null)
        );
    }

}
