package org.example.auctioncommon.domain.user.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserNewPasswordRequest(
    @field:NotBlank(message = "현재 비밀번호를 입력해 주세요")
    @field:Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    val currentPassword: String,

    @field:NotBlank(message = "변경할 비밀번호를 입력해 주세요")
    @field:Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    val newPassword:  String,

    @field:NotBlank(message = "변경할 비밀번호를 한 번 더 입력해 주세요")
    @field:Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    val confirmPassword: String
) {

}
