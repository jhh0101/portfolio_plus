package org.example.auctioncommon.domain.auction.repository

import jakarta.persistence.LockModeType
import org.example.auctioncommon.domain.auction.entity.Auction
import org.example.auctioncommon.domain.auction.entity.AuctionStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional


@Repository
interface AuctionRepository : JpaRepository<Auction, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
        ("SELECT a FROM Auction a " +
                "JOIN FETCH a.product p " +
                "JOIN FETCH p.seller s " +
                "WHERE a.auctionId = :auctionId")
    )
    fun findByIdWithPessimisticLock(@Param("auctionId") auctionId: Long): Auction?

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query(
        ("UPDATE Auction a SET a.status = :status " +
                "WHERE a.status = :proceedingStatus " +
                "AND a.endTime <= :now")
    )
    fun updateAuctionEnded(
        @Param("now") now: java.time.LocalDateTime,
        @Param("status") status: AuctionStatus,
        @Param("proceedingStatus") proceedingStatus: AuctionStatus
    )
}
