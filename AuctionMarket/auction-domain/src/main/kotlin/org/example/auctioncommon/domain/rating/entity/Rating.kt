package org.example.auctioncommon.domain.rating.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.example.auctioncommon.domain.order.entity.Order
import org.example.auctioncommon.domain.user.entity.User
import org.example.auctioncommon.global.base.BaseCreatedAt
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@SQLRestriction("status != 'DELETED'")
@SQLDelete(sql = "UPDATE seller_ratings SET status = 'DELETED' WHERE rating_id = ?")
@Entity
@Table(
    name = "seller_ratings",
    indexes = [jakarta.persistence.Index(
        name = "idx_ratings_to_user",
        columnList = "to_user_id"
    ), jakarta.persistence.Index(name = "idx_ratings_order", columnList = "order_id")]
)
class Rating(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    var ratingId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id")
    val toUser: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id")
    val fromUser: User,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    val order: Order,

    @Column(name = "score", nullable = false)
    var score: Int,

    @Column(name = "rating_comment", length = 100)
    var comment: String? = null,

    @Column(name = "status")
    @Enumerated(jakarta.persistence.EnumType.STRING)
    var status: RatingStatus,

) : BaseCreatedAt() {

    fun updateRating(score: Int, comment: String?) {
        this.score = score
        this.comment = comment
    }
}
