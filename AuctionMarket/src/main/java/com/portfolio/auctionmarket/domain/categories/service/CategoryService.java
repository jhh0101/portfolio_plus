package com.portfolio.auctionmarket.domain.categories.service;

import com.portfolio.auctionmarket.domain.auctions.entity.Auction;
import com.portfolio.auctionmarket.domain.auctions.repository.AuctionRepository;
import com.portfolio.auctionmarket.domain.categories.dto.CategoryRequest;
import com.portfolio.auctionmarket.domain.categories.dto.CategoryResponse;
import com.portfolio.auctionmarket.domain.categories.entity.Category;
import com.portfolio.auctionmarket.domain.categories.repository.CategoryRepository;
import com.portfolio.auctionmarket.domain.products.entity.Product;
import com.portfolio.auctionmarket.domain.products.repository.ProductRepository;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CategoryResponse addCategory(CategoryRequest request) {
        Category parent = null;
        String path;

        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
        }

        Category categorySave = Category.builder()
                .category(request.getCategory())
                .parent(parent)
                .build();

        Category category = categoryRepository.save(categorySave);

        if (parent == null) {
            path = String.valueOf(category.getCategoryId());
        } else {
            path = parent.getPath() + "/" + category.getCategoryId();
            parent.addChildrenCategory(category);
        }
        category.setPath(path);

        return CategoryResponse.from(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> searchCategory(Long parentId){
        List<Category> categories;
        if (parentId == null) {
            categories = categoryRepository.findByParentIsNull();
        } else {
            categories = categoryRepository.findByParent_CategoryId(parentId);
        }
        return categories.stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        String oldPath = category.getPath();
        String newPath;

        category.setCategory(request.getCategory());


        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
            category.setParent(parent);
            newPath = parent.getPath() + "/" + category.getCategoryId();
        } else {
            category.setParent(null);
            newPath = String.valueOf(category.getCategoryId());
        }
        category.setPath(newPath);

        categoryRepository.updatePathPrefix(oldPath + "/", newPath + "/");

        category.setCategory(request.getCategory());
        return CategoryResponse.from(category);
    }

    @Transactional
    public void deleteCategory(Long id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        if (!category.getChildren().isEmpty()) {
            throw new CustomException(ErrorCode.CATEGORY_HAS_CHILDREN);
        }

        // 연결 상품 존재 여부 확인 로직
        Product product = productRepository.findByCategory_CategoryId(id);

        if (product != null) {
            throw new CustomException(ErrorCode.CATEGORY_HAS_PRODUCT);
        }

        categoryRepository.delete(category);
    }

}
