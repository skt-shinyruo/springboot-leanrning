package com.learning.springboot.springcoreresources.part01_resource_abstraction;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

@Service
public class ResourceReadingService {

    private final ResourcePatternResolver resourcePatternResolver;

    public ResourceReadingService(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    public String readClasspathText(String location) {
        Resource resource = resourcePatternResolver.getResource(location);
        try (var in = resource.getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public List<String> listResourceLocations(String pattern) {
        try {
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            return Stream.of(resources)
                    .map(Resource::getDescription)
                    .sorted()
                    .toList();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}

