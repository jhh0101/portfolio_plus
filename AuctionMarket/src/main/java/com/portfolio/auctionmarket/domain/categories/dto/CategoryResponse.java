package com.portfolio.auctionmarket.domain.categories.dto;


import com.portfolio.auctionmarket.domain.categories.entity.Category;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private Long categoryId;
    private String path;
    private String category;
    private Long parentId;
    private List<CategoryResponse> children;

    public static CategoryResponse from(Category category) {
        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .path(category.getPath())
                .category(category.getCategory())
                .parentId(category.getParent() != null ? category.getParent().getCategoryId() : null)
                .children(category.getChildren() != null ?
                        category.getChildren()
                        .stream()
                        .map(CategoryResponse::from)
                        .collect(Collectors.toList())
                        : new ArrayList<>())
                .build();
    }
}
