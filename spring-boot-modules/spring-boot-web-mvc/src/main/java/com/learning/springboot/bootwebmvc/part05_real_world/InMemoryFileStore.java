package com.learning.springboot.bootwebmvc.part05_real_world;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Component
public class InMemoryFileStore {

    public record StoredFile(byte[] bytes, String fileName, String contentType) {
    }

    private final AtomicLong idSequence = new AtomicLong(0);
    private final Map<Long, StoredFile> store = new ConcurrentHashMap<>();

    public long save(MultipartFile file) {
        long id = idSequence.incrementAndGet();
        store.put(id, new StoredFile(readBytes(file), safeFileName(file), file.getContentType()));
        return id;
    }

    public Optional<StoredFile> find(long id) {
        return Optional.ofNullable(store.get(id));
    }

    public StoredFile getRequired(long id) {
        return find(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "file_not_found"));
    }

    private static byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new IllegalStateException("failed_to_read_upload", e);
        }
    }

    private static String safeFileName(MultipartFile file) {
        String name = file.getOriginalFilename();
        return (name == null || name.isBlank()) ? "unknown" : name;
    }
}
