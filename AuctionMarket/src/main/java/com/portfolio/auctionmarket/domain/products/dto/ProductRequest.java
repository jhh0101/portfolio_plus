package com.portfolio.auctionmarket.domain.products.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    private Long categoryId;
    @NotBlank(message = "상품 제목을 입력해주세요.")
    @Size(min = 2, max = 20, message = "제목은 2~20자 입니다.")
    private String title;
    private String description;
}
