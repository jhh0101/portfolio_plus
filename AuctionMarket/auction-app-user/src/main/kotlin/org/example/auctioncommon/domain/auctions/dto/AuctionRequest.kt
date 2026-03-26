package org.example.auctioncommon.domain.auctions.dto

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class AuctionRequest(
    @field:NotNull(message = "시작가를 입력해주세요.")
    var startPrice:Long,

    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @field:NotNull(message = "시작 기간을 입력해주세요.")
    var startTime:LocalDateTime,

    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    @field:NotNull(message = "종료 기간을 입력해주세요.")
    var endTime: LocalDateTime,
)
