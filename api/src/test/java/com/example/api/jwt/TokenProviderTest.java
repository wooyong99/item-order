package com.example.api.jwt;

import com.example.core.security.jwt.TokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TokenProviderTest {

    @Autowired
    TokenProvider tokenProvider;

    @Test
    public void createToken(){
        System.out.println(tokenProvider.createToken("1",false));
    }

}
