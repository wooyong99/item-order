package com.example.core.security.aop;

import com.example.core.domain.user.domain.UserRole;
import com.example.core.exception.AuthorizationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collection;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthorizationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        AuthorizationRequired annotation = getAnnotation(handler);

        if (annotation == null) {
            return true;
        }

        Collection<? extends GrantedAuthority> possibleAuthority = roleToAuthority(annotation.value());

        if (!hasAuthority(possibleAuthority)) {
            throw new AuthorizationException();
        }

        return true;
    }

    private boolean hasAuthority(Collection<? extends GrantedAuthority> possibleAuthority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication != null && authentication.getAuthorities()
                .stream().anyMatch(possibleAuthority::contains);
    }

    private AuthorizationRequired getAnnotation(Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        return handlerMethod.getMethodAnnotation(AuthorizationRequired.class);
    }

    private Collection<? extends GrantedAuthority> roleToAuthority(UserRole[] required) {
        return Arrays.stream(required)
                .map(Enum::name)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}

