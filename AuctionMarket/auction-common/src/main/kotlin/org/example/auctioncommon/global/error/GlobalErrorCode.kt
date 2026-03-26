package org.example.auctioncommon.global.error

enum class GlobalErrorCode(
    override val code: String,
    override val message: String
) : ErrorCode {
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


    // Auction


    // Bids


    // Orders

    // Rating
    RATING_NOT_FOUND("RATING001", "평가를 찾을 수 없습니다"),

    // Toss
    PAYMENT_NOT_DONE("TOSS001", "결제 승인 실패: 상태가 DONE이 아닙니다."),

    // Common
    BAD_REQUEST("C002", "잘못된 요청입니다"),
    RESOURCE_NOT_FOUND("C003", "리소스를 찾을 수 없습니다");

}