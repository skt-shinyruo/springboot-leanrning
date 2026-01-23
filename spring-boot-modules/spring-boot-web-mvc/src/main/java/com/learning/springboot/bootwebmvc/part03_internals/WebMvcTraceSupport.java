package com.learning.springboot.bootwebmvc.part03_internals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

final class WebMvcTraceSupport {

    static final String EVENTS_ATTR = WebMvcTraceSupport.class.getName() + ".events";

    private WebMvcTraceSupport() {
    }

    static void record(HttpServletRequest request, String event) {
        events(request).add(event);
    }

    static List<String> snapshot(HttpServletRequest request) {
        return List.copyOf(events(request));
    }

    static String dispatch(HttpServletRequest request) {
        if (request.getDispatcherType() == null) {
            return "UNKNOWN";
        }
        return request.getDispatcherType().name();
    }

    @SuppressWarnings("unchecked")
    static List<String> events(HttpServletRequest request) {
        Object raw = request.getAttribute(EVENTS_ATTR);
        if (raw instanceof List<?> existing) {
            return (List<String>) existing;
        }

        List<String> created = Collections.synchronizedList(new ArrayList<>());
        request.setAttribute(EVENTS_ATTR, created);
        return created;
    }
}

