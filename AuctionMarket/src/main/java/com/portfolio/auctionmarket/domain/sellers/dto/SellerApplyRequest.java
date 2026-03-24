package com.portfolio.auctionmarket.domain.sellers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SellerApplyRequest {
    @NotBlank(message = "상호명을 입력해주세요.")
    @Size(max = 50)
    private String storeName;

    @NotBlank(message = "은행 이름을 입력해주세요.")
    @Size(max = 20)
    private String bankName;

    @NotBlank(message = "계좌번호을 입력해주세요.")
    @Pattern(regexp = "^[0-9]{10,16}$", message = "계좌번호는 하이픈(-) 없이 10~16자리의 숫자로 입력해주세요.")
    private String accountNumber;

    @NotBlank(message = "예금주 이름을 입력해주세요.")
    @Size(max = 20)
    private String accountHolder;
}
