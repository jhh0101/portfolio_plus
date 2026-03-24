package com.portfolio.auctionmarket.domain.categories.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    private String category;
    private Long parentId;
}
