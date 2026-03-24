package com.portfolio.auctionmarket.auth.controller;

import com.portfolio.auctionmarket.auth.dto.LoginRequest;
import com.portfolio.auctionmarket.auth.dto.RefreshTokenRequest;
import com.portfolio.auctionmarket.auth.dto.SecurityUser;
import com.portfolio.auctionmarket.auth.dto.TokenResponse;
import com.portfolio.auctionmarket.auth.service.AuthService;
import com.portfolio.auctionmarket.global.error.LoginException;
import com.portfolio.auctionmarket.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        TokenResponse tokenResponse = authService.login(request);
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .path("/")
                .secure(false)
                .httpOnly(true)
                .sameSite("Lax")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        response.setHeader("Set-Cookie", responseCookie.toString());
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", tokenResponse.getAccessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@AuthenticationPrincipal SecurityUser user) {
        authService.logout(user.getUserId());
        return ResponseEntity.ok(ApiResponse.success("로그아웃 성공"));
    }

    @PostMapping("/refresh")
    public ApiResponse<String> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {

        if (refreshToken == null) {
            throw new RuntimeException("리프레시 토큰이 없습니다.");
        }

        TokenResponse tokenResponse = authService.refreshAccessToken(refreshToken);
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .path("/")
                .secure(false)                                // https 환경에서만 쿠키가 발동합니다.
                .sameSite("Lax")                           // 동일 사이트과 크로스 사이트에 모두 쿠키 전송이 가능합니다
                .httpOnly(true)                              // 브라우저에서 쿠키에 접근할 수 없도록 제한
                .maxAge(7 * 24 * 60 * 60)
                .build();

        response.setHeader("Set-Cookie", responseCookie.toString());
        return ApiResponse.success("토큰 갱신 성공", tokenResponse.getAccessToken());
    }

}
