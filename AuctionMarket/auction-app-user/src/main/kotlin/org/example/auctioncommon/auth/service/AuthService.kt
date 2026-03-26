package org.example.auctioncommon.auth.service

import org.example.auctioncommon.auth.dto.LoginRequest
import org.example.auctioncommon.auth.dto.SecurityUser
import org.example.auctioncommon.auth.dto.TokenResponse
import org.example.auctioncommon.domain.user.entity.User
import org.example.auctioncommon.domain.user.error.UserErrorCode
import org.example.auctioncommon.domain.user.repository.UserRepository
import org.example.auctioncommon.global.config.JwtProperties
import org.example.auctioncommon.global.error.CustomException
import org.example.auctioncommon.global.error.GlobalErrorCode
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.LockedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class AuthService(
    private val jwtService: JwtService,
    private val jwtProperties: JwtProperties,
    private val authProcessor: AuthProcessor,
    private val userRepository: UserRepository,
    private val refreshTokenService: RefreshTokenService,
    private val authenticationManager: AuthenticationManager,
) {


    @Transactional
    fun login(request: LoginRequest): TokenResponse {
        try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    request.email,
                    request.password
                )
            )

            val securityUser: SecurityUser? = authentication.principal as SecurityUser?

            val accessToken: String = jwtService.generateAccessToken(
                userId = securityUser?.userId,
                email = securityUser?.username.toString(),
                nickname = securityUser?.nickname.toString(),
                role = securityUser?.user?.role?.name.toString()
            )
            val refreshToken: String = jwtService.generateRefreshToken(securityUser?.userId ?: 0L)

            refreshTokenService.saveRefreshTokenBidirectional(securityUser?.userId ?: 0L, refreshToken)

            return TokenResponse(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresIn = jwtProperties.accessTokenExpiration?.div(1000L)
            )
        } catch (e: LockedException) {
            throw CustomException(UserErrorCode.SUSPENDED_USER, "정지된 사용자 입니다.")
        }
    }

    fun refreshAccessToken(refreshToken: String): TokenResponse {
        jwtService.validateToken(refreshToken)

        val userId: Long =
            refreshTokenService.getUserIdByToken(refreshToken) ?: throw CustomException(GlobalErrorCode.TOKEN_NOT_FOUND)

        val redisRefreshToken: String? = refreshTokenService.redisRefreshToken(userId)

        authProcessor.validateRedisRefreshToken(redisRefreshToken, refreshToken)

        if (redisRefreshToken == null || redisRefreshToken != refreshToken) {
            throw CustomException(GlobalErrorCode.INVALID_TOKEN)
        }

        val user: User = userRepository.findByIdOrNull(userId)
            ?: throw CustomException(UserErrorCode.USER_NOT_FOUND)

        val newAccessToken: String =
            jwtService.generateAccessToken(userId, user.email, user.nickname, user.role.name)
        val newRefreshToken: String = jwtService.generateRefreshToken(user.userId)

        refreshTokenService.saveRefreshTokenBidirectional(userId, newRefreshToken)

        return TokenResponse(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken,
            expiresIn = jwtProperties.accessTokenExpiration?.div(1000L)
        )
    }

    fun logout(userId: Long) {
        refreshTokenService.deleteRefreshToken(userId)
    }
}
