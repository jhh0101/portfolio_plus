package com.portfolio.auctionmarket.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE("C001", "잘못된 입력 값입니다"),
    INVALID_VERIFICATION_CODE("C400", "유효하지 않은 인증 코드입니다"),
    EXPIRED_VERIFICATION_CODE("C401", "인증 코드가 만료되었습니다"),
    INTERNAL_SERVER_ERROR("C999", "서버 내부 오류가 발생했습니다"),

    // Auth
    INVALID_TOKEN("AUTH001", "유효하지 않은 토큰입니다"),
    EXPIRED_TOKEN("AUTH002", "만료된 토큰입니다"),
    UNAUTHORIZED("AUTH003", "인증이 필요합니다"),
    INVALID_CREDENTIALS("AUTH004", "아이디 또는 비밀번호가 일치하지 않습니다"),
    TOKEN_NOT_FOUND("AUTH005", "토큰을 찾을 수 없습니다"),
    LOGIN_FAILED("AUTH006", "로그인 실패"),

    // User
    USER_NOT_FOUND("USER001", "사용자를 찾을 수 없습니다"),
    DUPLICATE_EMAIL("USER002", "이미 사용 중인 이메일입니다"),
    DUPLICATE_NICKNAME("USER003", "이미 사용 중인 사용자 이름입니다"),
    USER_VERIFICATION_FAILED("USER004", "사용자 인증에 실패했습니다"),
    PASSWORD_MISMATCH("USER005", "비밀번호가 일치하지 않습니다"),
    SOCIAL_USER_CANNOT_CHANGE_PASSWORD("USER006", "소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다"),
    SUSPENDED_USER("USER007", "정지된 사용자입니다"),
    WITHDRAWN_USER("USER008", "탈퇴된 사용자입니다"),
    CANNOT_WITHDRAW_WHILE_TRADING("USER009", "진행 중인 거래가 있습니다"),
    PROTECT_DEFAULT_USERS("USER010", "기본 사용자는 삭제하거나 비밀번호 변경할 수 없습니다"),

    // Seller
    SELLER_NOT_FOUND("SELLER001", "판매자를 찾을 수 없습니다."),
    DUPLICATE_SELLER("SELLER002", "이미 신청 중인 사용자입니다."),

    // Category
    CATEGORY_NOT_FOUND("CATEGORY001", "카테고리를 찾을 수 없습니다"),
    CATEGORY_HAS_CHILDREN("CATEGORY002", "하위 카테고리가 존재합니다"),
    CATEGORY_HAS_PRODUCT("CATEGORY003", "등록된 상품이 존재합니다"),

    // Image
    IMAGE_NOT_FOUND("IMAGE001", "이미지를 찾을 수 없습니다"),
    IMAGE_IS_MAIN("IMAGE002", "이미 메인 이미지입니다"),
    IMAGE_UPLOAD_FAILED("IMAGE003", "이미지 업로드에 실패했습니다"),
    INVALID_IMAGE_FORMAT("IMAGE004", "지원하지 않는 이미지 형식입니다"),

    // Product
    PRODUCT_NOT_FOUND("PRODUCT001", "상품을 찾을 수 없습니다"),
    CANNOT_MODIFY_AFTER_BID("PRODUCT002", "입찰한 상품은 수정할 수 없습니다"),
    CANNOT_DELETE_AFTER_BID("PRODUCT003", "입찰한 상품은 삭제할 수 없습니다"),

    // Auction
    AUCTION_NOT_FOUND("A001", "경매를 찾을 수 없습니다"),
    AUCTION_ENDED("A002", "이미 종료된 경매입니다"),
    INVALID_AUCTION_TIME("A003", "지금은 경매 시간이 아닙니다"),

    // Bids
    BID_NOT_FOUND("B001", "입찰 내역을 찾을 수 없습니다"),
    BID_PRICE_TOO_LOW("B002", "입찰가는 현재가보다 높아야 합니다"),
    SELF_BID_NOT_ALLOWED("B003", "자신의 상품에는 입찰할 수 없습니다"),
    NOT_ENOUGH_POINTS("B004", "포인트가 부족합니다"),
    ALREADY_HIGHEST_BIDDER("B005", "이미 입찰 중 입니다."),
    BID_CANCEL_RESTRICTED("B006", "입찰 후 10분이 지났거나 경매 마감 10분 전에는 취소가 불가능합니다"),

    // Orders
    ORDER_NOT_FOUND("ORDER001", "주문 내역을 찾을 수 없습니다"),

    // Rating
    RATING_NOT_FOUND("RATING001", "평가를 찾을 수 없습니다"),

    // Toss
    PAYMENT_NOT_DONE("TOSS001", "결제 승인 실패: 상태가 DONE이 아닙니다."),

    // Common
    BAD_REQUEST("C002", "잘못된 요청입니다"),
    RESOURCE_NOT_FOUND("C003", "리소스를 찾을 수 없습니다");

    private final String code;
    private final String message;
}
