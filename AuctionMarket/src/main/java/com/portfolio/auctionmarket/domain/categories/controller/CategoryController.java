package com.portfolio.auctionmarket.domain.categories.controller;

import com.portfolio.auctionmarket.domain.categories.dto.CategoryRequest;
import com.portfolio.auctionmarket.domain.categories.dto.CategoryResponse;
import com.portfolio.auctionmarket.domain.categories.service.CategoryService;
import com.portfolio.auctionmarket.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> addCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.addCategory(request);
        return ResponseEntity.ok(ApiResponse.success("카테고리 생성", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> searchCategory(@RequestParam(required = false) Long parentId) {
        List<CategoryResponse> response = categoryService.searchCategory(parentId);
        return ResponseEntity.ok(ApiResponse.success("카테고리 조회", response));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@PathVariable Long id,
                                                                        @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("카테고리 수정", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("카테고리 삭제", null));
    }
}
