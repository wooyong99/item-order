package com.example.api.domain.user.service;

import com.example.api.domain.user.dto.SignupRequest;
import com.example.api.domain.user.dto.TokenResponse;
import com.example.api.domain.user.dto.UserDetailsImpl;
import com.example.core.domain.user.domain.User;
import com.example.core.domain.user.repository.UserRepository;
import com.example.core.exception.LoginFailedException;
import com.example.core.exception.UserAlreadyExistsException;
import com.example.core.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final PasswordEncoder encoder;

    private final TokenProvider tokenProvider;

    @Transactional
    public TokenResponse login(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(email, password);
//        AuthenticationToken authenticationToken = new AuthenticationToken(email, password);

        Authentication authentication;
        try {
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            throw new LoginFailedException("로그인 정보가 일치하지 않습니다");
        } catch (InternalAuthenticationServiceException e) {
            throw new LoginFailedException("존재하지 않는 유저입니다");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        System.out.println(((UserDetailsImpl) userDetails).getId());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return TokenResponse.builder()
            .token(tokenProvider.createToken(((UserDetailsImpl) userDetails).getId(), false))
            .refreshToken(tokenProvider.createToken(((UserDetailsImpl) userDetails).getId(), true))
            .build();
    }

    @Transactional
    public void signup(SignupRequest request) {
        validationSignup(request.getEmail());

        User user = User.builder().email(request.getEmail())
            .password(encoder.encode(request.getPassword()))
            .build();
        userRepository.save(user);
    }

    private void validationSignup(String email) {
        existsUserByEmail(email);
    }
    private void existsUserByEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException();
        }
    }
}

