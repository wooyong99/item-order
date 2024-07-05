package com.example.core.security;

import com.example.core.domain.user.domain.User;
import com.example.core.domain.user.domain.UserRole;
import java.util.Collection;
import java.util.Set;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
public class AuthenticationToken extends UsernamePasswordAuthenticationToken {

    private User user;

    public AuthenticationToken(User user) {
        super(user, null, authorities(user.getRole()));
        this.user = user;
    }

    private static Collection<? extends GrantedAuthority> authorities(UserRole role) {
        return Set.of(new SimpleGrantedAuthority(role.name()));
    }

//    @Override
//    public Map<String, Object> getAttributes() {
//        return null;
//    }

    @Override
    public String getName() {
        return String.valueOf(user.getId());
    }
}