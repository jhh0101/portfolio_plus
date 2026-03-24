package com.portfolio.auctionmarket.domain.products.controller;

import com.portfolio.auctionmarket.auth.dto.SecurityUser;
import com.portfolio.auctionmarket.domain.products.dto.*;
import com.portfolio.auctionmarket.domain.products.service.ProductService;
import com.portfolio.auctionmarket.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@Slf4j
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 상품 추가
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> addProduct(@Valid @RequestBody ProductAndAuctionRequest request,
                                                                   @AuthenticationPrincipal SecurityUser user) {
        ProductResponse response = productService.addProduct(user.getUserId(), request.getProductRequest(), request.getAuctionRequest());
        return ResponseEntity.ok(ApiResponse.success("상품 등록", response));
    }

    // 상품 전체 리스트 조회
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductAndAuctionResponse>>> productList(ProductListCondition condition,
                                                                                    @PageableDefault(size = 9) Pageable pageable) {
        Page<ProductAndAuctionResponse> responses = productService.productList(condition, pageable);
        return ResponseEntity.ok(ApiResponse.success("상품 리스트 조회", responses));
    }

    // 상품 상세 조회
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDetailAndAuctionResponse>> findProductDetail(@PathVariable Long productId, HttpServletResponse httpResponse, HttpServletRequest request, @AuthenticationPrincipal Long userId) {
        ProductDetailAndAuctionResponse response = productService.findProductDetail(productId, request, httpResponse, userId);
        return ResponseEntity.ok(ApiResponse.success("상품 단건 조회", response));
    }

    // 판매자(자신) 상품 리스트 조회
    @GetMapping("/my-product")
    public ResponseEntity<ApiResponse<Page<ProductAndAuctionResponse>>> myProductList(@AuthenticationPrincipal SecurityUser user,
                                                                                      ProductListCondition condition,
                                                                                      @PageableDefault(size = 5) Pageable pageable) {
        Page<ProductAndAuctionResponse> responses = productService.myProductList(user.getUserId(), condition, pageable);
        return ResponseEntity.ok(ApiResponse.success("나의 상품 리스트 조회", responses));
    }

    // 상품 상세 수정
    @PatchMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDetailAndAuctionResponse>> updateProduct(@AuthenticationPrincipal SecurityUser user,
                                                                                      @PathVariable Long productId,
                                                                                      @Valid @RequestBody ProductAndAuctionRequest request) {
        ProductDetailAndAuctionResponse response = productService.updateProductDetail(user.getUserId(), productId, request.getProductRequest(), request.getAuctionRequest());
        return ResponseEntity.ok(ApiResponse.success("상품 상세 수정", response));
    }

    // 상품 삭제
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@AuthenticationPrincipal SecurityUser user,
                                                           @PathVariable Long productId) {
        productService.deleteProduct(user.getUserId(), productId);
        return ResponseEntity.ok(ApiResponse.success("상품 & 이미지 삭제", null));
    }

    // 이미지 메서드
    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<ProductImageResponse>>> uploadImages(@PathVariable("id") Long productId, // 경로에서 상품 ID 추출
                                                                                @RequestPart("files") List<MultipartFile> files){
        List<ProductImageResponse> response = productService.uploadImages(productId, files);
        return ResponseEntity.ok(ApiResponse.success("이미지 업로드", response));
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<ApiResponse<List<ProductImageResponse>>> loadImages(@PathVariable("id") Long productId){
        List<ProductImageResponse> responses = productService.loadImage(productId);
        return ResponseEntity.ok(ApiResponse.success("이미지 로드", responses));
    }

    @PatchMapping("/{id}/images")
    public ResponseEntity<ApiResponse<Void>> moveToMain(@PathVariable("id") Long id) {
        productService.moveToMain(id);
        return ResponseEntity.ok(ApiResponse.success("이미지 메인 변경", null));
    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable("id") Long imageId) {
        productService.deleteImage(imageId);
        return ResponseEntity.ok(ApiResponse.success("이미지 삭제", null));
    }

}
