package com.senawiki.visit.service;

import com.senawiki.visit.domain.VisitRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class VisitTrackingFilter extends OncePerRequestFilter {

    private final VisitRepository visitRepository;

    public VisitTrackingFilter(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        if (shouldTrack(request)) {
            recordVisit(request);
        }
        filterChain.doFilter(request, response);
    }

    private boolean shouldTrack(HttpServletRequest request) {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        String path = request.getRequestURI();
        if (path == null) {
            return false;
        }
        if (path.startsWith("/api/admin")) {
            return false;
        }
        if ("/error".equals(path)) {
            return false;
        }
        return path.startsWith("/api/");
    }

    private void recordVisit(HttpServletRequest request) {
        visitRepository.insertIfNotExists(buildVisitorKey(request), LocalDate.now());
    }

    private String buildVisitorKey(HttpServletRequest request) {
        String ip = resolveClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        String raw = ip + "|" + (userAgent == null ? "" : userAgent);
        return sha256Hex(raw);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            String[] parts = forwarded.split(",");
            if (parts.length > 0) {
                return parts[0].trim();
            }
        }
        return request.getRemoteAddr();
    }

    private String sha256Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : hashed) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 not available", ex);
        }
    }
}
