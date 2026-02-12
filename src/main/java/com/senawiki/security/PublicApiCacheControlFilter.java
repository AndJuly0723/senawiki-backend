package com.senawiki.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class PublicApiCacheControlFilter extends OncePerRequestFilter {

    private static final int PUBLIC_CACHE_MAX_AGE_SECONDS = (int) Duration.ofMinutes(1).getSeconds();
    private static final int PUBLIC_CACHE_STALE_WHILE_REVALIDATE_SECONDS = (int) Duration.ofMinutes(2).getSeconds();

    private final RequestMatcher publicApiMatcher = new OrRequestMatcher(List.of(
        new AntPathRequestMatcher("/api/community/**", HttpMethod.GET.name()),
        new AntPathRequestMatcher("/api/tip/**", HttpMethod.GET.name()),
        new AntPathRequestMatcher("/api/boards/**", HttpMethod.GET.name()),
        new AntPathRequestMatcher("/api/heroes/**", HttpMethod.GET.name()),
        new AntPathRequestMatcher("/api/pets/**", HttpMethod.GET.name()),
        new AntPathRequestMatcher("/api/guide-decks/**", HttpMethod.GET.name()),
        new AntPathRequestMatcher("/api/guide-decks/*/equipment", HttpMethod.GET.name())
    ));

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        filterChain.doFilter(request, response);
        applyCacheHeaders(request, response);
    }

    private void applyCacheHeaders(HttpServletRequest request, HttpServletResponse response) {
        if (isCacheablePublicGet(request) && isCacheableStatus(response)) {
            response.setHeader(
                HttpHeaders.CACHE_CONTROL,
                "public, max-age=" + PUBLIC_CACHE_MAX_AGE_SECONDS
                    + ", s-maxage=" + PUBLIC_CACHE_MAX_AGE_SECONDS
                    + ", stale-while-revalidate=" + PUBLIC_CACHE_STALE_WHILE_REVALIDATE_SECONDS
            );
            response.setHeader(HttpHeaders.PRAGMA, "");
            response.setDateHeader(HttpHeaders.EXPIRES, System.currentTimeMillis() + (PUBLIC_CACHE_MAX_AGE_SECONDS * 1000L));
            return;
        }

        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, max-age=0, must-revalidate");
        response.setHeader(HttpHeaders.PRAGMA, "no-cache");
        response.setDateHeader(HttpHeaders.EXPIRES, 0);
    }

    private boolean isCacheablePublicGet(HttpServletRequest request) {
        return HttpMethod.GET.matches(request.getMethod()) && publicApiMatcher.matches(request);
    }

    private boolean isCacheableStatus(HttpServletResponse response) {
        int status = response.getStatus();
        return (status >= 200 && status < 300) || status == HttpServletResponse.SC_NOT_MODIFIED;
    }
}
