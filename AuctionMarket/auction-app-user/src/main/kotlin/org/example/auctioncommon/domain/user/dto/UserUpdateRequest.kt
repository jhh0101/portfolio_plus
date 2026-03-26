package org.example.auctioncommon.domain.user.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UserUpdateRequest(
    @field:NotBlank(message = "사용자 닉네임을 입력해 주세요.")
    @field:Size(min = 5, max = 20, message = "닉네임은 5~20자 입니다.")
    val nickname: String,

    @field:NotBlank(message = "전화번호를 입력해주세요.")
    @field:Pattern(regexp = "^010\\d{8}$", message = "전화번호는 숫자만 입력해 주세요.")
    val phone: String,

    @field:NotBlank(message = "집 주소를 입력해주세요.")
    val baseAddress: String,
    val detailAddress: String? = null
)

fun UserUpdateRequest.toUpdateCommand() : UserUpdateCommand {
    return UserUpdateCommand(
        nickname = this.nickname,
        phone = this.phone,
        baseAddress = this.baseAddress,
        detailAddress = this.detailAddress
    )
}