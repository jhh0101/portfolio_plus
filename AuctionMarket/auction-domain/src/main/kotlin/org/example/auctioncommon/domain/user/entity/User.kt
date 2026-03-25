package org.example.auctioncommon.domain.user.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.example.auctioncommon.global.base.Base
import org.example.auctioncommon.global.error.CustomException
import org.example.auctioncommon.global.error.ErrorCode

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val userId: Long? = null,

    @Column(name = "email", unique = true, nullable = false)
    var email: String,

    @Column(name = "password")
    var password: String? = null,

    @Column(name = "username", nullable = false)
    var username: String,

    @Column(name = "nickname", unique = true, nullable = false)
    var nickname: String,

    @Column(name = "base_address")
    var baseAddress: String? = null,

    @Column(name = "detail_address")
    var detailAddress: String? = null,

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    var role: Role,

    @Column(name = "point", nullable = false)
    var point: Long = 0L,

    @Column(name = "avg_rating", nullable = false)
    var avgRating: Double = 0.0,

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: UserStatus,

    @Column(name = "phone", unique = true, nullable = false)
    var phone: String,

    @Column(name = "suspension_reason")
    var suspensionReason: String? = null,

) : Base() {


    fun subPoint(point: Long) {
        if (this.point < point) {
            throw CustomException(ErrorCode.NOT_ENOUGH_POINTS)
        }
        this.point -= point
    }

    fun addPoint(point: Long) {
        this.point += point
    }

    fun withdraw(maskedEmail: String, maskedUsername: String, maskedPhone: String, userId: Long?) {
        this.email = maskedEmail + "_" + userId
        this.username = maskedUsername
        this.phone = maskedPhone
        this.nickname = "탈퇴된 사용자 $userId"
        this.password = null
        this.baseAddress = null
        this.detailAddress = null
        this.point = 0L
        this.avgRating = 0.0
        this.suspensionReason = null
        this.status = UserStatus.WITHDRAWN
        this.role = Role.USER
    }

    fun suspend(userId: Long, suspensionReason: String) {
        this.nickname = "정지된 사용자 $userId"
        this.suspensionReason = suspensionReason
        this.status = UserStatus.SUSPENDED
        this.role = Role.USER
    }

    fun updateUser(phone: String, nickname: String) {
        this.phone = phone
        this.nickname = nickname
    }

    fun updatePassword(newPassword: String) {
        this.password = newPassword
    }

    fun updateRole(role: Role) {
        this.role = role
    }

    fun updateRating(avgRating: Double) {
        this.avgRating = avgRating
    }
}
