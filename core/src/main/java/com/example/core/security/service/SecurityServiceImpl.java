package com.example.core.security.service;

import com.example.core.domain.user.domain.User;
import com.example.core.domain.user.repository.UserRepository;
import com.example.core.exception.InternalServerException;
import com.example.core.security.AuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final UserRepository userRepository;

    @Override
    public Authentication getAuthentication(String userId) {
        User user = getUser(userId);

        return createAuthentication(user);
    }

    private UsernamePasswordAuthenticationToken createAuthentication(User user) {
        return new AuthenticationToken(user);
    }

    private User getUser(String userId) {
        long id = Long.parseLong(userId);

        return userRepository.findById(id).orElseThrow(
            () -> new InternalServerException()
        );
    }

//    private GrantedAuthority userRoleToAuthorities(UserRole userRole) {
//        return new SimpleGrantedAuthority(userRole.name());
//    }
}




