package com.example.api.domain.user;

import com.example.core.domain.user.domain.User;

public class Util {

    public static User createUser(String email, String password, String nickname) {
        return User.builder()
            .email(email)
            .password(password)
            .nickname(nickname)
            .build();
    }

}
