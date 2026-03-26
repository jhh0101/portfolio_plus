package org.example.auctioncommon.auth.service

import RefreshTokenRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RefreshTokenService(
    private val redisTemplate: RedisTemplate<String, String>,

    @field:Value("\${jwt.refresh-token-expiration:604800000}")
    private var refreshTokenExpiration: Long = 0L
) : RefreshTokenRepository{

    override fun saveRefreshTokenBidirectional(userId: Long, token: String) {
        val userKey = REFRESH_TOKEN_PREFIX + "user:" + userId
        val tokenKey: String = REFRESH_TOKEN_PREFIX + "token:" + token

        val oldToken = redisTemplate.opsForValue().get(userKey)
        if (oldToken != null) {
            redisTemplate.delete(REFRESH_TOKEN_PREFIX + "token:" + oldToken)
        }

        redisTemplate.opsForValue().set(userKey, token, refreshTokenExpiration, TimeUnit.MILLISECONDS)
        redisTemplate.opsForValue().set(tokenKey, userId.toString(), refreshTokenExpiration, TimeUnit.MILLISECONDS)
    }

    override fun getUserIdByToken(token: String): Long? {
        val key = REFRESH_TOKEN_PREFIX + "token:" + token
        val userId = redisTemplate.opsForValue().get(key)
        return userId?.toLong()
    }

    override fun deleteRefreshToken(userId: Long) {
        val userKey = REFRESH_TOKEN_PREFIX + "user:" + userId
        val token = redisTemplate.opsForValue().get(userKey)

        if (token != null) {
            redisTemplate.delete(REFRESH_TOKEN_PREFIX + "token:" + token)
        }
        redisTemplate.delete(userKey)
    }

    override fun redisRefreshToken(userId: Long) : String? {
        return redisTemplate.opsForValue().get("refresh:user:$userId")
    }

    companion object {
        private const val REFRESH_TOKEN_PREFIX = "refresh:"
    }
}