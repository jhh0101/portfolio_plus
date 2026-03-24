package com.portfolio.auctionmarket.domain.user.entity;

import com.portfolio.auctionmarket.domain.sellers.entity.Seller;
import com.portfolio.auctionmarket.domain.user.dto.UserNewPasswordRequest;
import com.portfolio.auctionmarket.domain.user.dto.UserUpdateRequest;
import com.portfolio.auctionmarket.global.base.Base;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "username")
    private String username;

    @Column(name = "nickname", unique = true)
    private String nickname;

    @Column(name = "base_address")
    private String baseAddress;

    @Column(name = "detail_address")
    private String detailAddress;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "point")
    private Long point;

    @Column(name = "avg_rating")
    private Double avgRating;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "phone", unique = true)
    private String phone;

    @Column(name = "suspension_reason")
    private String suspensionReason;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Seller seller;

    public void subPoint(Long point) {
        if (this.point < point) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_POINTS);
        }
        this.point -= point;
    }

    public void addPoint(Long point) {
        this.point += point;
    }

    public void withdraw(String maskedEmail, String maskedUsername, String maskedPhone, Long userId) {
        this.email = maskedEmail + "_" + userId;
        this.username = maskedUsername;
        this.phone = maskedPhone;
        this.nickname = "탈퇴된 사용자" + userId;
        this.password = null;
        this.baseAddress = null;
        this.detailAddress = null;
        this.point = 0L;
        this.avgRating = 0.0;
        this.suspensionReason = null;
        this.status = UserStatus.WITHDRAWN;
        this.role = Role.USER;
    }

    public void  suspend(Long userId, String suspensionReason) {
        this.nickname = "정지된 사용자" + userId;
        this.suspensionReason = suspensionReason;
        this.status = UserStatus.SUSPENDED;
        this.role = Role.USER;
    }

    public void updateUser(UserUpdateRequest request) {
        this.phone = request.getPhone();
        this.nickname = request.getNickname();
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateRole(Role role){
        this.role = role;
    }

    public void updateRating(Double avgRating) {
        this.avgRating = avgRating;
    }
}
