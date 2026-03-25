package org.example.auctioncommon.domain.seller.repository

import org.example.auctioncommon.domain.seller.entity.Seller
import org.example.auctioncommon.domain.seller.entity.SellerStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface SellerRepository : JpaRepository<Seller, Long> {
    fun findByUser_UserId(userId: Long): Optional<Seller>

    fun findAllByStatus(status: SellerStatus, pageable: Pageable): Page<Seller>
}
