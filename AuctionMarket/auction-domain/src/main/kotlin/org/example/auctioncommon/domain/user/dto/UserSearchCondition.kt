package org.example.auctioncommon.domain.user.dto

import org.example.auctioncommon.domain.user.entity.Role
import org.example.auctioncommon.domain.user.entity.UserStatus

class UserSearchCondition(
    val email: String? = null,
    val nickname: String? = null,
    val status: UserStatus? = null,
    val role: Role? = null,
)