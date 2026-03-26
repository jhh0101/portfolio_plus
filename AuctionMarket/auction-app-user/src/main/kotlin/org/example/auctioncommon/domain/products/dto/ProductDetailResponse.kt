package org.example.auctioncommon.domain.products.dto

import com.fasterxml.jackson.annotation.JsonFormat
import org.example.auctioncommon.domain.product.entity.Product
import org.example.auctioncommon.domain.product.entity.ProductStatus
import org.example.auctioncommon.domain.user.entity.User

data class ProductDetailResponse(
    val productId: Long,
    val seller: String,
    val sellerId: Long,
    val category: String,
    val categoryId: Long,
    val title: String,
    val description: String,
    val viewCount: Int,
    val ratingScore: Double,
    val productStatus: ProductStatus,

    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private val createdAt: java.time.LocalDateTime? = null
)
fun Product.toProductDetailDto(user: User?) : ProductDetailResponse {
    return ProductDetailResponse(
        productId = this.productId ?: 0L,
        seller = this.seller.nickname,
        sellerId = this.seller.userId ?: 0L,
        category = this.category.category,
        categoryId = this.category.categoryId ?: 0L,
        title = this.title,
        description = this.description,
        viewCount = this.viewCount,
        productStatus = this.productStatus,
        createdAt = this.createdAt,
        ratingScore = user?.avgRating ?: 0.0
    )
}