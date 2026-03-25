package org.example.auctioncommon.global.util

class MaskingUtil private constructor() {
    init {
        throw AssertionError()
    }

    companion object {
        fun formatPhone(phone: String?): String? {
            // 숫자만 있는 문자열을 3-4-4 포맷으로 변경
            return phone?.replaceFirst("(\\d{3})(\\d{3,4})(\\d{4})".toRegex(), "$1-$2-$3")
        }

        fun maskEmail(email: String?): String? {
            return email?.replace("(?<=.{2}).(?=.*@)".toRegex(), "*")
        }

        fun maskPhone(phone: String?): String? {
            return phone?.replace("(\\d{2,3})-\\d{3,4}-(\\d{4})".toRegex(), "$1-****-$2")
        }

        fun maskUsername(username: String?): String? {
            if (username == null || username.length < 2) return username
            return username[0] + "*".repeat(username.length - 1)
        }
    }
}
