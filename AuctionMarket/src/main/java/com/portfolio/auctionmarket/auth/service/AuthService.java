package com.portfolio.auctionmarket.auth.service;

import com.portfolio.auctionmarket.auth.dto.LoginRequest;
import com.portfolio.auctionmarket.auth.dto.SecurityUser;
import com.portfolio.auctionmarket.auth.dto.TokenResponse;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.domain.user.entity.UserStatus;
import com.portfolio.auctionmarket.domain.user.repository.UserRepository;
import com.portfolio.auctionmarket.global.config.JwtProperties;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public TokenResponse login(LoginRequest request) {

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();

            String accessToken = jwtService.generateAccessToken(
                    securityUser.getUserId(),
                    securityUser.getUsername(),
                    securityUser.getNickname(),
                    securityUser.getUser().getRole().name()
            );
            String refreshToken = jwtService.generateRefreshToken(securityUser.getUserId());

            refreshTokenService.saveRefreshTokenBidirectional(securityUser.getUserId(), refreshToken);

            return TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .expiresIn(jwtProperties.getAccessTokenExpiration() / 1000)
                    .tokenType("Bearer")
                    .build();
        } catch (LockedException e){
            throw new CustomException(ErrorCode.SUSPENDED_USER, "정지된 사용자 입니다.");
        }
    }

    public TokenResponse refreshAccessToken(String refreshToken) {
        jwtService.validateToken(refreshToken);

        Long userId = refreshTokenService.getUserIdByToken(refreshToken);

        if (userId == null) {
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
        }

        String redisRefreshToken = redisTemplate.opsForValue().get("refresh:user:" + userId);

        if (redisRefreshToken == null || !redisRefreshToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtService.generateAccessToken(user.getUserId(), user.getEmail(), user.getNickname(), user.getRole().name());
        String newRefreshToken = jwtService.generateRefreshToken(user.getUserId());

        refreshTokenService.saveRefreshTokenBidirectional(user.getUserId(), newRefreshToken);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtProperties.getAccessTokenExpiration() / 1000)
                .build();
    }

    public void logout(Long userId) {
        refreshTokenService.deleteRefreshToken(userId);
    }
}
