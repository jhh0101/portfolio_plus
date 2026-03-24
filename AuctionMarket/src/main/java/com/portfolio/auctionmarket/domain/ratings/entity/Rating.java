package com.portfolio.auctionmarket.domain.ratings.entity;

import com.portfolio.auctionmarket.domain.orders.entity.Order;
import com.portfolio.auctionmarket.domain.ratings.dto.RatingRequest;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.global.base.BaseCreatedAt;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("status != 'DELETED'")
@SQLDelete(sql = "UPDATE seller_ratings SET status = 'DELETED' WHERE rating_id = ?")
@Entity
@Table(name = "seller_ratings", indexes = {
        @Index(name = "idx_ratings_to_user", columnList = "to_user_id"),
        @Index(name = "idx_ratings_order", columnList = "order_id")
})
@Builder
public class Rating extends BaseCreatedAt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Long ratingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id")
    private User toUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id")
    private User fromUser;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "rating_comment", length = 100)
    private String comment;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RatingStatus status;

    public void updateRating(RatingRequest request) {
        this.score = request.getScore();
        this.comment = request.getComment();
    }

}
