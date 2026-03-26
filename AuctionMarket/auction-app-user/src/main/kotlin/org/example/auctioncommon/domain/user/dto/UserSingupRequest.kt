package org.example.auctioncommon.domain.user.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UserSingupRequest(
    @field:NotBlank(message = "이메일을 입력해주세요.")
    @field:Email(message = "올바른 이메일 형식을 입력해주세요.")
    val email: String,

    @field:NotBlank(message = "사용자 이름을 입력해주세요.")
    @field:Size(min = 2, max = 20, message = "이름은 2~20자 입니다.")
    val username: String,

    @field:NotBlank(message = "사용자 닉네임을 입력해주세요.")
    @field:Size(min = 5, max = 20, message = "닉네임은 5~20자 입니다.")
    val nickname: String,

    @field:NotBlank(message = "전화번호를 입력해주세요.")
    @field:Pattern(regexp = "^010\\d{8}$", message = "전화번호는 숫자만 입력해주세요.")
    val phone: String,

    @field:NotBlank(message = "집 주소를 입력해주세요.")
    val baseAddress: String,
    val detailAddress: String? = null,

    @field:NotBlank(message = "비밀번호를 입력해 주세요")
    @field:Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    val password: String,
)