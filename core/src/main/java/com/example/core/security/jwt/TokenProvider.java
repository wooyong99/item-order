package com.example.core.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.token-validity-in-seconds}")
    private long tokenValidityInSeconds;
    private Key key;

    @PostConstruct
    void init(){
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String createToken(String id, boolean isRefresh) {
        return Jwts.builder()
                .setSubject(id)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(getExpiration(isRefresh))
                .setIssuedAt(new Date())
                .compact();
    }

    public String refresh(String refreshToken) {
        Claims claims = getClaims(refreshToken);
        return createToken(claims.getSubject(), true);
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        Claims claims;
        try {
            JwtParser jwtParser = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build();
            claims = jwtParser
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SecurityException | MalformedJwtException e) {
            throw new MalformedJwtException("잘못된 JWT 서명입니다");
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), "만료된 JWT 토큰입니다");
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException("지원되지 않는 JWT 토큰입니다");
        } catch (IllegalArgumentException e) {
            throw new MalformedJwtException("JWT 토큰이 잘못되었습니다");
        }
        return claims;
    }

    private Date getExpiration(boolean isRefresh) {
        long nowMilliSeconds = new Date().getTime();
        long tokenValidityInMilliSeconds = tokenValidityInSeconds * 1000;
        if (isRefresh) {
            tokenValidityInMilliSeconds *= 30;
        }
        long expirationMilliSeconds = nowMilliSeconds + tokenValidityInMilliSeconds;
        return new Date(expirationMilliSeconds);
    }
}
