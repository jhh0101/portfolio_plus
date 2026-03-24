package com.portfolio.auctionmarket.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    @NotBlank(message = "사용자 닉네임을 입력해 주세요.")
    @Size(min = 5, max = 20, message = "닉네임은 5~20자 입니다.")
    private String nickname;

    @NotBlank(message = "전화번호를 입력해주세요.")
    @Pattern(regexp = "^010\\d{8}$", message = "전화번호는 숫자만 입력해 주세요.")
    private String phone;

    @NotBlank(message = "집 주소를 입력해주세요.")
    private String baseAddress;
    private String detailAddress;

}
