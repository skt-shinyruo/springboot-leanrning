package com.learning.springboot.bootwebmvc.part05_real_world;

import java.util.Map;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/advanced/files")
public class FileTransferController {

    private final InMemoryFileStore fileStore;

    public FileTransferController(InMemoryFileStore fileStore) {
        this.fileStore = fileStore;
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file) {
        long id = fileStore.save(file);
        return Map.of(
                "id", id,
                "fileName", file.getOriginalFilename(),
                "size", file.getSize(),
                "contentType", file.getContentType()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> download(@PathVariable("id") long id) {
        InMemoryFileStore.StoredFile stored = fileStore.getRequired(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename(stored.fileName()).build());
        headers.setContentType(parseContentType(stored.contentType()));

        return ResponseEntity.ok()
                .headers(headers)
                .body(stored.bytes());
    }

    private static MediaType parseContentType(String raw) {
        if (raw == null || raw.isBlank()) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
        try {
            return MediaType.parseMediaType(raw);
        } catch (Exception ignored) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}

