package com.portfolio.auctionmarket.domain.sellers.entity;

import com.portfolio.auctionmarket.domain.sellers.dto.SellerApplyRequest;
import com.portfolio.auctionmarket.domain.user.entity.Role;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.global.base.BaseCreatedAt;
import jakarta.persistence.*;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "sellers")
@Builder
@AttributeOverride(name = "createdAt", column = @Column(name = "applied_at",updatable = false))
public class Seller extends BaseCreatedAt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seller_id")
    private Long sellerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "store_name", length = 50)
    private String storeName;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "account_number", length = 20)
    private String accountNumber;

    @Column(name = "account_holder")
    private String accountHolder;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private SellerStatus status;

    @Column(name = "reject_reason")
    private String rejectReason;

    public void updateApply(User user, SellerApplyRequest request) {
        this.user = user;
        this.storeName = request.getStoreName();
        this.bankName = request.getBankName();
        this.accountNumber = request.getAccountNumber();
        this.accountHolder = request.getAccountHolder();
        this.status = SellerStatus.PENDING;
        this.rejectReason = null;
    }

    public void cancelSeller() {
        this.storeName = "";
        this.bankName = "";
        this.accountNumber = "";
        this.accountHolder = "";
        this.status = SellerStatus.CANCELED;
        this.rejectReason = "사용자에 의한 신청 취소";

        if (this.user != null) {
            this.user.updateRole(Role.USER);
        }
    }

    public void approveSeller() {
        this.status = SellerStatus.APPROVED;
        this.rejectReason = null;

        if (this.user != null) {
            this.user.updateRole(Role.SELLER);
        }
    }

    public void rejectSeller(String rejectReason) {
        this.status = SellerStatus.REJECTED;
        this.rejectReason = rejectReason;
    }

}
