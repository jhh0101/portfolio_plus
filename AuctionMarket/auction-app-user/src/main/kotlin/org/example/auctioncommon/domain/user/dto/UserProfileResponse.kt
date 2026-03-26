package org.example.auctioncommon.domain.user.dto

import com.fasterxml.jackson.annotation.JsonFormat
import org.example.auctioncommon.domain.seller.entity.Seller
import org.example.auctioncommon.domain.seller.entity.SellerStatus
import org.example.auctioncommon.domain.user.entity.Role
import org.example.auctioncommon.domain.user.entity.User
import org.example.auctioncommon.global.util.MaskingUtil
import java.time.LocalDateTime

data class UserProfileResponse(
    val userId: Long,
    val email: String,
    val username: String,
    val phone: String,
    val nickname: String,
    val baseAddress: String,
    val detailAddress: String,
    val role: Role,
    val point: Long,
    val avgRating: String,
    val sellerStatus: SellerStatus,

    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private val createdAt: LocalDateTime? = null,

    @field:JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private val updatedAt: LocalDateTime? = null,
)

fun User.toProfileDto() : UserProfileResponse{
    val formatPhone: String? = MaskingUtil.formatPhone(phone)
    val seller: Seller? = seller
    val sellerStatus: SellerStatus? = if (seller != null) seller.status else SellerStatus.NONE
    val detailAddress: String? = if (detailAddress != null) detailAddress else ""
    val formatRating: String = String.format("%.1f", avgRating)

    return UserProfileResponse(
        userId = this.userId ?: 0L,
        email = this.email,
        username = this.username,
        phone = formatPhone.toString(),
        nickname = this.nickname,
        baseAddress = this.baseAddress,
        detailAddress = detailAddress.toString(),
        role = this.role,
        point = this.point,
        avgRating = formatRating,
        sellerStatus = sellerStatus ?: SellerStatus.NONE,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt

    )
}