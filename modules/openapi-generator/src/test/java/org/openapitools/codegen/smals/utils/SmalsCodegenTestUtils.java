package org.openapitools.codegen.smals.utils;

import lombok.Getter;
import org.openapitools.codegen.ClientOptInput;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.config.CodegenConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SmalsCodegenTestUtils {

    public static final Logger LOGGER = LoggerFactory.getLogger(SmalsCodegenTestUtils.class);
    public static Map<String, File> generateFromContract(String url, Generator generator, Map<String, Object> additionalProperties,
                                                         Consumer<CodegenConfigurator> consumer) throws IOException {

        File output = Files.createTempDirectory("test").toFile().getCanonicalFile();
        LOGGER.info(output.getAbsolutePath());
        output.deleteOnExit();

        final CodegenConfigurator configurator = new CodegenConfigurator()
                .setGeneratorName(generator.generatorName)
                .setAdditionalProperties(additionalProperties)
                .setValidateSpec(false)
                .setInputSpec(url)
                .setOutputDir(output.getAbsolutePath());
        if (null != generator.getLibrary()) {
            configurator.setLibrary(generator.getLibrary());
        }
        consumer.accept(configurator);

        ClientOptInput input = configurator.toClientOptInput();
        DefaultGenerator defaultGenerator = new DefaultGenerator();
        defaultGenerator.setGenerateMetadata(false);

        return defaultGenerator.opts(input).generate().stream()
                .collect(Collectors.toMap(SmalsCodegenTestUtils::getUniqueName, Function.identity()));
    }

    private static String getUniqueName(File file) {
        String name = file.getName();
        if ("package-info.java".equals(name)) {
            return file.getParentFile().getName() + "/" + name;
        }
        return name;
    }


    @Getter
    public enum Generator {
        SPRING_HTTP_INTERFACE("spring", "spring-http-interface"),
        SPRING_BOOT("spring", "spring-boot"),
        JAVA_REST_CLIENT("java", "restclient"),
        JAVA_REST_EASY("java", "resteasy"),
        JAVA_REST_TEMPLATE("java", "resttemplate"),
        JAVA_WEB_CLIENT("java", "webclient"),
        JAXRS_DEFAULT("jaxrs-spec", null);

        private final String generatorName;
        private final String library;

        Generator(String generatorName, String library) {
            this.generatorName = generatorName;
            this.library = library;
        }

    }
}
