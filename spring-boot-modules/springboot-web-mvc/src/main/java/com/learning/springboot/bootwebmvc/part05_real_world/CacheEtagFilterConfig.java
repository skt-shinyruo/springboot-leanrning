package com.learning.springboot.bootwebmvc.part05_real_world;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
public class CacheEtagFilterConfig {

    @Bean
    public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
        return new ScopedShallowEtagHeaderFilter();
    }

    /**
     * 风险控制：避免对全站请求开启 ETag 计算（教学用仅绑定到指定端点）。
     */
    static class ScopedShallowEtagHeaderFilter extends ShallowEtagHeaderFilter {

        @Override
        protected boolean shouldNotFilter(HttpServletRequest request) {
            String uri = request.getRequestURI();
            return uri == null || !uri.equals("/api/advanced/cache/filter-etag");
        }
    }
}

