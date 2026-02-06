package com.senawiki.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null) {
            log.debug("Missing Authorization header for {} {}", request.getMethod(), request.getRequestURI());
        } else if (!header.toLowerCase(Locale.ROOT).startsWith("bearer ")) {
            log.debug("Non-bearer Authorization header for {} {} (prefix={})",
                request.getMethod(),
                request.getRequestURI(),
                header.length() > 12 ? header.substring(0, 12) : header
            );
        } else {
            String token = header.substring(7).trim();
            if (jwtTokenProvider.validateToken(token)) {
                String email = jwtTokenProvider.getSubject(token);
                var userDetails = userDetailsService.loadUserByUsername(email);

                var authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                log.debug("Invalid JWT for {} {} (prefix={})",
                    request.getMethod(),
                    request.getRequestURI(),
                    token.length() > 10 ? token.substring(0, 10) : token
                );
            }
        }

        filterChain.doFilter(request, response);
    }
}
