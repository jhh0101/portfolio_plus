package org.example.auctioncommon.domain.user.dto

import com.fasterxml.jackson.annotation.JsonFormat
import org.example.auctioncommon.domain.seller.entity.Seller
import org.example.auctioncommon.domain.seller.entity.SellerStatus
import org.example.auctioncommon.domain.user.entity.Role
import org.example.auctioncommon.domain.user.entity.User
import org.example.auctioncommon.domain.user.entity.UserStatus
import org.example.auctioncommon.global.util.MaskingUtil
import java.time.LocalDateTime
import kotlin.Long

data class UserResponse(
    var userId: Long? = null,
    var email: String,
    var username: String,
    var phone: String,
    var nickname: String,
    var baseAddress: String,
    var detailAddress: String? = null,
    var role: Role,
    var sellerStatus: SellerStatus? = null,
    var userStatus: UserStatus,
    var point: Long,
    var avgRating: Double,

    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime? = null,
    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val updatedAt: LocalDateTime? = null
)

fun User.toDto() : UserResponse {
    val formatPhone = MaskingUtil.formatPhone(phone)
    val seller: Seller? = seller
    val sellerStatus: SellerStatus? = if (seller != null) seller.status else SellerStatus.NONE

    return UserResponse(
        userId = this.userId,
        email = this.email,
        username = this.username,
        phone = formatPhone.toString(),
        nickname = this.nickname,
        baseAddress = this.baseAddress,
        detailAddress = this.detailAddress,
        role = this.role,
        sellerStatus = sellerStatus,
        userStatus = this.status,
        point = this.point,
        avgRating = this.avgRating,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}