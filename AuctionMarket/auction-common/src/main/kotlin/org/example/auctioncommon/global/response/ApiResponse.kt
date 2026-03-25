package org.example.auctioncommon.global.response

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class ApiResponse<T> (
    val success: Boolean,
    val code: String,
    val message: String,
    val data: T? = null
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> =
            ApiResponse(true, "C000", "성공", data)

        fun <T> success(message: String, data: T? = null): ApiResponse<T> =
            ApiResponse(true, "C000", message, data)

        fun error(code: String, message: String): ApiResponse<Nothing> =
            ApiResponse(false, code, message, null)
    }
}
