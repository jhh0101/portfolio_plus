package org.example.auctioncommon.domain.product.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(
    name = "product_images",
    indexes = [Index(name = "idx_product_image_product", columnList = "product_id")]
)
class ProductImage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    var imageId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: Product? = null,

    @Column(name = "image_url")
    var imageUrl: String,

    @Column(name = "image_order")
    var imageOrder: Int
) {

    fun updateOrder(newOrder: Int) {
        this.imageOrder = newOrder
    }
}
