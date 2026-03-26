package org.example.auctioncommon.domain.products.dto

import org.example.auctioncommon.domain.product.entity.ProductImage

data class ProductImageResponse(
    val imageId: Long,
    val productId: Long,
    val imageUrl: String,
    val imageOrder: Int,
)
fun ProductImage.toProductImageDto() : ProductImageResponse{
    return ProductImageResponse(
        imageId = this.imageId ?: 0L,
        productId = this.product?.productId ?: 0L,
        imageUrl = this.imageUrl,
        imageOrder = this.imageOrder
    )
}