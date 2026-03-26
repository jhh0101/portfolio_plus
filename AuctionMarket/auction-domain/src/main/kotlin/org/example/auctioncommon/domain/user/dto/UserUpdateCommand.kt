package org.example.auctioncommon.domain.user.dto

data class UserUpdateCommand(
    val nickname: String,
    val phone: String,
    val baseAddress: String,
    val detailAddress: String? = null
)
