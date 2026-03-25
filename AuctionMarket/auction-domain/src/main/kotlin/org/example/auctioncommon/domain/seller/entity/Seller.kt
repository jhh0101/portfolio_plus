package org.example.auctioncommon.domain.seller.entity

import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.example.auctioncommon.domain.seller.dto.SellerApplyCommand
import org.example.auctioncommon.domain.user.entity.Role
import org.example.auctioncommon.domain.user.entity.User
import org.example.auctioncommon.global.base.BaseCreatedAt

@Entity
@Table(name = "sellers")
@AttributeOverride(name = "createdAt", column = Column(name = "applied_at", updatable = false))
class Seller(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seller_id")
    var sellerId: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    var user: User,

    @Column(name = "store_name", length = 50)
    var storeName: String? = null,

    @Column(name = "bank_name")
    var bankName: String? = null,

    @Column(name = "account_number", length = 20)
    var accountNumber: String? = null,

    @Column(name = "account_holder")
    var accountHolder: String? = null,

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status: SellerStatus? = null,

    @Column(name = "reject_reason")
    var rejectReason: String? = null

) : BaseCreatedAt() {


    fun updateApply(command: SellerApplyCommand) {
        this.storeName = command.storeName
        this.bankName = command.bankName
        this.accountNumber = command.accountNumber
        this.accountHolder = command.accountHolder
        this.status = SellerStatus.PENDING
        this.rejectReason = null
    }

    fun cancelSeller() {
        this.storeName = ""
        this.bankName = ""
        this.accountNumber = ""
        this.accountHolder = ""
        this.status = SellerStatus.CANCELED
        this.rejectReason = "사용자에 의한 신청 취소"
        this.user.updateRole(Role.USER)
    }

    fun approveSeller() {
        this.status = SellerStatus.APPROVED
        this.rejectReason = null
        this.user.updateRole(Role.SELLER)
    }

    fun rejectSeller(rejectReason: String?) {
        this.status = SellerStatus.REJECTED
        this.rejectReason = rejectReason
    }
}
