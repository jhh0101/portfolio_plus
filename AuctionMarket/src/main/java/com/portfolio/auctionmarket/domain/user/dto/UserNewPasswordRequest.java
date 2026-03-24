package com.portfolio.auctionmarket.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserNewPasswordRequest {

    @NotBlank(message = "현재 비밀번호를 입력해 주세요")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    private String currentPassword;

    @NotBlank(message = "변경할 비밀번호를 입력해 주세요")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    private String newPassword;

    @NotBlank(message = "변경할 비밀번호를 한 번 더 입력해 주세요")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    private String confirmPassword;


}
