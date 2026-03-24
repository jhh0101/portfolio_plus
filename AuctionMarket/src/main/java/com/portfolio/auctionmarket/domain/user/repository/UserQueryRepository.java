package com.portfolio.auctionmarket.domain.user.repository;


import com.portfolio.auctionmarket.domain.user.dto.UserListCondition;
import com.portfolio.auctionmarket.domain.user.entity.Role;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.domain.user.entity.UserStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.portfolio.auctionmarket.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;;

    public Page<User> userList(UserListCondition condition, Pageable pageable) {
        List<User> content = jpaQueryFactory
                .selectFrom(user)
                .where(
                        user.role.ne(Role.ADMIN),
                        emailContain(condition.email()),
                        nicknameContain(condition.nickname()),
                        statusContain(condition.status()),
                        roleContain(condition.role())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(user.userId.asc())
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory
                .select(user.count())
                .from(user)
                .where(
                        emailContain(condition.email()),
                        nicknameContain(condition.nickname()),
                        statusContain(condition.status())
                );

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    private BooleanExpression emailContain(String email) {
        return StringUtils.hasText(email) ? user.email.startsWith(email) : null;
    }
    private BooleanExpression nicknameContain(String nickname) {
        return StringUtils.hasText(nickname) ? user.nickname.contains(nickname) : null;
    }
    private BooleanExpression statusContain(UserStatus status) {
        return status != null ? user.status.eq(status) : null;
    }
    private BooleanExpression roleContain(Role role) {
        return role != null ? user.role.eq(role) : null;
    }
}
