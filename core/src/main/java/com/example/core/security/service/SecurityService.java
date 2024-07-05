package com.example.core.security.service;

import org.springframework.security.core.Authentication;

public interface SecurityService {

    Authentication getAuthentication(String userId);
}
