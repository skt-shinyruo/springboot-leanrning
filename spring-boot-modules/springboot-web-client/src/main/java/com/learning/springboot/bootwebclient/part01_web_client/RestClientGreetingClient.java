package com.learning.springboot.bootwebclient.part01_web_client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

public class RestClientGreetingClient {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final RestClient restClient;

    public RestClientGreetingClient(String baseUrl, Duration readTimeout, String correlationId) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(readTimeout);

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultHeader("X-Correlation-Id", correlationId)
                .build();
    }

    public GreetingResponse getGreeting(String name) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/greeting").queryParam("name", name).build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw toDownstreamException(response.getStatusCode(), response.getBody());
                })
                .body(GreetingResponse.class);
    }

    public GreetingResponse createGreeting(String name) {
        return restClient.post()
                .uri("/api/greeting")
                .body(new GreetingRequest(name))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                    throw toDownstreamException(response.getStatusCode(), response.getBody());
                })
                .body(GreetingResponse.class);
    }

    public GreetingResponse getGreetingWithRetryOn5xx(String name, int maxAttempts) {
        int attempts = 0;
        while (true) {
            attempts += 1;
            try {
                return getGreeting(name);
            } catch (DownstreamServiceException e) {
                if (attempts >= maxAttempts) {
                    throw e;
                }
                if (e.getStatus() >= 500 && e.getStatus() < 600) {
                    continue;
                }
                throw e;
            }
        }
    }

    private static DownstreamServiceException toDownstreamException(HttpStatusCode statusCode, java.io.InputStream bodyStream) {
        String body = readBody(bodyStream);
        String message = parseMessage(body);
        return new DownstreamServiceException(statusCode.value(), message, body);
    }

    private static String readBody(java.io.InputStream bodyStream) {
        if (bodyStream == null) {
            return "";
        }
        try {
            return new String(bodyStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            return "";
        }
    }

    private static String parseMessage(String body) {
        if (body == null || body.isBlank()) {
            return "downstream_error";
        }
        try {
            JsonNode root = OBJECT_MAPPER.readTree(body);
            JsonNode message = root.get("message");
            if (message != null && message.isTextual() && !message.asText().isBlank()) {
                return message.asText();
            }
        } catch (Exception ignored) {
        }
        return "downstream_error";
    }

    private record GreetingRequest(String name) {}
}
