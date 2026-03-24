package com.portfolio.auctionmarket.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";

    public void saveRefreshTokenBidirectional(Long userId, String token) {
        String userKey = REFRESH_TOKEN_PREFIX + "user:" + userId;
        String tokenKey = REFRESH_TOKEN_PREFIX + "token:" + token;

        String oldToken = redisTemplate.opsForValue().get(userKey);
        if (oldToken != null) {
            redisTemplate.delete(REFRESH_TOKEN_PREFIX + "token:" + oldToken);
        }

        redisTemplate.opsForValue().set(userKey, token, refreshTokenExpiration, TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue().set(tokenKey, String.valueOf(userId), refreshTokenExpiration, TimeUnit.MILLISECONDS);
    }

    public Long getUserIdByToken(String token){
        String key = REFRESH_TOKEN_PREFIX + "token:" + token;
        String userId = redisTemplate.opsForValue().get(key);
        return userId != null ? Long.parseLong(userId) : null;
    }

    public void deleteRefreshToken(Long userId) {
        String userKey = REFRESH_TOKEN_PREFIX + "user:" + userId;
        String token = redisTemplate.opsForValue().get(userKey);

        if (token != null) {
            redisTemplate.delete(REFRESH_TOKEN_PREFIX + "token:" + token);
        }
        redisTemplate.delete(userKey);
    }
}