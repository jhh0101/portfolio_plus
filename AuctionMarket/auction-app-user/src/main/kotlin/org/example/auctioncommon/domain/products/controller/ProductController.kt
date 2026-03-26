package org.example.auctioncommon.domain.products.controller

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.example.auctioncommon.domain.product.dto.ProductAndAuctionResponse
import org.example.auctioncommon.domain.product.dto.ProductListCondition
import org.example.auctioncommon.domain.products.dto.ProductDetailAndAuctionResponse
import org.example.auctioncommon.domain.products.service.ProductService
import org.example.auctioncommon.global.response.ApiResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/product")
class ProductController(
    private val productService: ProductService
) {

    @GetMapping
    fun productList(
        condition: ProductListCondition,
        @PageableDefault(size = 9) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<ProductAndAuctionResponse>>> {
        val responses: Page<ProductAndAuctionResponse> = productService.productList(condition, pageable)
        return ResponseEntity.ok(ApiResponse.success("상품 리스트 조회", responses))
    }

    // 상품 상세 조회
    @GetMapping("/{productId}")
    fun findProductDetail(
        @PathVariable productId: Long,
        @AuthenticationPrincipal userId: Long,
        @CookieValue(name = "viewed_products", required = false) viewCookie: String,
        response: HttpServletResponse
    ): ResponseEntity<ApiResponse<ProductDetailAndAuctionResponse>> {

        val result = productService.findProductDetail(productId, userId, viewCookie)

        result.newCookieValue?.let {
            val newCookie = Cookie("viewed_products", it).apply {
                path = "/"
                maxAge = 60 * 60 * 24
                isHttpOnly = true
            }
            response.addCookie(newCookie)
        }

        return ResponseEntity.ok(ApiResponse.success("상품 단건 조회", result.response))
    }
}
