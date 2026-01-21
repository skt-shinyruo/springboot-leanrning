package com.learning.springboot.bootwebmvc.part04_contract;

import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StrictJsonMessageConverterConfig implements WebMvcConfigurer {

    public static final String STRICT_JSON_VALUE = "application/vnd.learning.strict+json";
    public static final MediaType STRICT_JSON = MediaType.valueOf(STRICT_JSON_VALUE);

    public static class StrictJsonMessageConverter extends MappingJackson2HttpMessageConverter {

        public StrictJsonMessageConverter(ObjectMapper objectMapper) {
            super(objectMapper);
        }
    }

    private final StrictJsonMessageConverter strictConverter;

    public StrictJsonMessageConverterConfig(ObjectMapper objectMapper) {
        ObjectMapper strictObjectMapper = objectMapper.copy();
        strictObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        StrictJsonMessageConverter converter = new StrictJsonMessageConverter(strictObjectMapper);
        converter.setSupportedMediaTypes(List.of(STRICT_JSON));
        this.strictConverter = converter;
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, strictConverter);
    }
}
