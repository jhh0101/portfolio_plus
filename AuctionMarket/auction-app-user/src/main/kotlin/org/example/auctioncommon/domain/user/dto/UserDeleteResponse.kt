package org.example.auctioncommon.domain.user.dto

import org.example.auctioncommon.domain.user.entity.User

data class UserDeleteResponse(
    val email: String
)

fun User.toDeleteDto() : UserDeleteResponse {
    return UserDeleteResponse(
        email = this.email
    )
}