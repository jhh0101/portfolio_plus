package org.example.auctioncommon.domain.user.repository

import jakarta.persistence.LockModeType
import org.example.auctioncommon.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun existsByNickname(nickname: String): Boolean

    fun findByEmail(email: String): User?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.userId = :userId")
    fun findByIdWithPessimisticLock(@Param("userId") userId: Long): User?
}
