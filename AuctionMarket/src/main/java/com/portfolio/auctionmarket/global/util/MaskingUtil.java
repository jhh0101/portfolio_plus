package com.portfolio.auctionmarket.global.util;

public class MaskingUtil {
    public static String formatPhone(String phone) {
        if (phone == null) return null;
        // 숫자만 있는 문자열을 3-4-4 포맷으로 변경
        return phone.replaceFirst("(\\d{3})(\\d{3,4})(\\d{4})", "$1-$2-$3");
    }

    private MaskingUtil(){
        throw new AssertionError();
    }

    public static String maskEmail(String email) {
        if (email == null) return null;
        return email.replaceAll("(?<=.{2}).(?=.*@)", "*");
    }

    public static String maskPhone(String phone) {
        if (phone == null) return null;
        return phone.replaceAll("(\\d{2,3})-\\d{3,4}-(\\d{4})", "$1-****-$2");
    }

    public static String maskUsername(String username) {
        if (username == null || username.length() < 2) return username;
        return username.charAt(0) + "*".repeat(username.length() - 1);
    }
}
