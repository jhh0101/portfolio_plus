package com.portfolio.auctionmarket.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSingupRequest {
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    private String email;

    @NotBlank(message = "사용자 이름을 입력해주세요.")
    @Size(min = 2, max = 20, message = "이름은 2~20자 입니다.")
    private String username;

    @NotBlank(message = "사용자 닉네임을 입력해주세요.")
    @Size(min = 5, max = 20, message = "닉네임은 5~20자 입니다.")
    private String nickname;

    @NotBlank(message = "전화번호를 입력해주세요.")
    @Pattern(regexp = "^010\\d{8}$", message = "전화번호는 숫자만 입력해주세요.")
    private String phone;

    @NotBlank(message = "집 주소를 입력해주세요.")
    private String baseAddress;
    private String detailAddress;

    @NotBlank(message = "비밀번호를 입력해 주세요")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    private String password;

}
