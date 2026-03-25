package org.example.auctioncommon.domain.user.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.example.auctioncommon.domain.user.dto.UserSearchCondition
import org.example.auctioncommon.domain.user.entity.Role
import org.example.auctioncommon.domain.user.entity.User
import org.example.auctioncommon.domain.user.entity.UserStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

import org.example.auctioncommon.domain.user.entity.QUser.user

@Repository
class UserQueryRepository(
    private val jpaQueryFactory: JPAQueryFactory
) {

    fun userList(condition: UserSearchCondition, pageable: Pageable): Page<User> {
        val content = jpaQueryFactory
            .selectFrom(user)
            .where(
                user.role.ne(Role.ADMIN),
                emailContain(condition.email),
                nicknameContain(condition.nickname),
                statusContain(condition.status),
                roleContain(condition.role)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(user.userId.asc())
            .fetch()

        val count = jpaQueryFactory
            .select(user.count())
            .from(user)
            .where(
                emailContain(condition.email),
                nicknameContain(condition.nickname),
                statusContain(condition.status)
            )

        return PageableExecutionUtils.getPage(content, pageable) { count.fetchOne() ?: 0L }

    }

    private fun emailContain(email: String?): BooleanExpression? {
        return if (!email.isNullOrBlank()) user.email.startsWith(email) else null
    }

    private fun nicknameContain(nickname: String?): BooleanExpression? {
        return if (!nickname.isNullOrBlank()) user.nickname.contains(nickname) else null
    }

    private fun statusContain(status: UserStatus?): BooleanExpression? {
        return status?.let { user.status.eq(it) }
    }

    private fun roleContain(role: Role?): BooleanExpression? {
        return role?.let { user.role.eq(it) }
    }
}
