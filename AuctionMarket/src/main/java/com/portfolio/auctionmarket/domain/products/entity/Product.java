package com.portfolio.auctionmarket.domain.products.entity;

import com.portfolio.auctionmarket.domain.auctions.entity.Auction;
import com.portfolio.auctionmarket.domain.categories.entity.Category;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.global.base.BaseCreatedAt;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@SQLRestriction("status != 'DELETED'")
@SQLDelete(sql = "UPDATE products SET status = 'DELETED' WHERE product_id = ?")
@Table(name = "products", indexes = {
        @Index(name = "idx_product_seller", columnList = "seller_id"),
        @Index(name = "idx_product_category", columnList = "category_id")
})
@Builder
public class Product extends BaseCreatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    @OneToOne(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Auction auction;

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> image = new ArrayList<>();

    public void updateProduct(Category category, String title, String description, Long startPrice, LocalDateTime startTime, LocalDateTime endTime) {
        this.category = category;
        this.title = title;
        this.description = description;

        if (auction != null) {
            this.auction.updateAuction(startPrice, startTime, endTime);
        }
    }

    public void changeStatus(ProductStatus status) {
        this.productStatus = status;
    }
}
