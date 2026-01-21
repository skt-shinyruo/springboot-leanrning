package com.learning.springboot.springcorebeans.part01_ioc_container;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.UUID;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class BeansDemoRunner implements ApplicationRunner {

    private static final String KEY_PREFIX = "BEANS:";

    private final FormattingService formattingService;
    private final DirectPrototypeConsumer directPrototypeConsumer;
    private final ProviderPrototypeConsumer providerPrototypeConsumer;
    private final LifecycleLogger lifecycleLogger;
    private final ApplicationContext applicationContext;
    private final Environment environment;

    public BeansDemoRunner(
            FormattingService formattingService,
            DirectPrototypeConsumer directPrototypeConsumer,
            ProviderPrototypeConsumer providerPrototypeConsumer,
            LifecycleLogger lifecycleLogger,
            ApplicationContext applicationContext,
            Environment environment
    ) {
        this.formattingService = formattingService;
        this.directPrototypeConsumer = directPrototypeConsumer;
        this.providerPrototypeConsumer = providerPrototypeConsumer;
        this.lifecycleLogger = lifecycleLogger;
        this.applicationContext = applicationContext;
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("== spring-core-beans ==");

        System.out.println(KEY_PREFIX + "activeProfiles=" + formatActiveProfiles(environment.getActiveProfiles()));
        System.out.println(KEY_PREFIX + "beanDefinitionCount=" + applicationContext.getBeanDefinitionCount());
        System.out.println(KEY_PREFIX + "textFormatters=" + formatTextFormatters(applicationContext.getBeansOfType(TextFormatter.class)));

        System.out.println(KEY_PREFIX + "formattingService.injectedFormatter=" + formattingService.formatterImplementation());
        System.out.println(KEY_PREFIX + "format(\"Hello\")=" + formattingService.format("Hello"));

        UUID direct1 = directPrototypeConsumer.currentId();
        UUID direct2 = directPrototypeConsumer.currentId();
        System.out.println(KEY_PREFIX + "prototype.direct.sameId=" + direct1.equals(direct2));

        UUID provider1 = providerPrototypeConsumer.newId();
        UUID provider2 = providerPrototypeConsumer.newId();
        System.out.println(KEY_PREFIX + "prototype.provider.differentId=" + !provider1.equals(provider2));

        System.out.println(KEY_PREFIX + "lifecycle.postConstructCalled=" + lifecycleLogger.isInitialized());
    }

    private static String formatActiveProfiles(String[] profiles) {
        if (profiles == null || profiles.length == 0) {
            return "(none)";
        }
        return Arrays.stream(profiles)
                .sorted()
                .collect(Collectors.joining(","));
    }

    private static String formatTextFormatters(Map<String, TextFormatter> formatters) {
        if (formatters == null || formatters.isEmpty()) {
            return "(none)";
        }

        return formatters.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                .map(entry -> entry.getKey() + ":" + entry.getValue().getClass().getSimpleName())
                .collect(Collectors.joining(","));
    }
}
