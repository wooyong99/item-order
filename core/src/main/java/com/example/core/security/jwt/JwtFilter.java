package com.example.core.security.jwt;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.example.core.security.service.SecurityService;
import com.example.core.util.HttpServletUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String key;

    @Value("${jwt.token-validity-in-seconds}")
    private String accessTokenExpiration;

    @Value("${jwt.token-validity-in-seconds}")
    private String refreshTokenExpiration;

    private final TokenProvider tokenProvider;

    private final HttpServletUtils servletUtils;

    private final SecurityService securityService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String accessToken = getAccessToken(request);
        String refreshToken = getRefreshToken(request);

        if (tokenProvider.validateToken(accessToken)) {
            setAuthentication(accessToken);
        } else if (refreshToken != null && tokenProvider.validateToken(refreshToken)) {
            String subject = tokenProvider.getSubject(refreshToken);

            accessToken = tokenProvider.createToken(subject, false);
            refreshToken = tokenProvider.createToken(subject, false);

            servletUtils.putHeader(response, AUTHORIZATION, accessToken);
            servletUtils.addCookie(response, "RefreshToken", refreshToken,
                Integer.parseInt(refreshTokenExpiration));

            setAuthentication(accessToken);
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String accessToken) {
        Authentication authentication = securityService.getAuthentication(tokenProvider.getSubject(accessToken));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String getAccessToken(HttpServletRequest request) {
        return servletUtils.getHeader(request, AUTHORIZATION).orElse(null);
    }

    private String getRefreshToken(HttpServletRequest request) {
        return servletUtils.getCookie(request, "RefreshToken")
            .map(Cookie::getValue)
            .orElse(null);
    }
}


