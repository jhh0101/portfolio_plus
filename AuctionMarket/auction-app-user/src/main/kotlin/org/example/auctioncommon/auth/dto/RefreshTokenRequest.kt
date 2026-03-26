package org.example.auctioncommon.auth.dto

import jakarta.validation.constraints.NotBlank

data class RefreshTokenRequest(
    @field:NotBlank(message = "리프레시 토큰을 입력해주세요")
    val refreshToken:  String
)