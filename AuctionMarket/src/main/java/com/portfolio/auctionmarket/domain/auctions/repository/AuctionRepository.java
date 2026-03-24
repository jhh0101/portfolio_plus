package com.portfolio.auctionmarket.domain.auctions.repository;

import com.portfolio.auctionmarket.domain.auctions.entity.Auction;
import com.portfolio.auctionmarket.domain.auctions.entity.AuctionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Auction a " +
            "JOIN FETCH a.product p " +
            "JOIN FETCH p.seller s " +
            "WHERE a.auctionId = :auctionId")
    Optional<Auction> findByIdWithPessimisticLock(@Param("auctionId") Long auctionId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Query("UPDATE Auction a SET a.status = :status " +
            "WHERE a.status = :proceedingStatus " +
            "AND a.endTime <= :now")
    void updateAuctionEnded(@Param("now") LocalDateTime now,
                            @Param("status") AuctionStatus status,
                            @Param("proceedingStatus") AuctionStatus proceedingStatus);
}
