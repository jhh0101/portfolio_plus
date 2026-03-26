package org.example.auctioncommon.domain.orders.controller

import org.example.auctioncommon.auth.dto.SecurityUser
import org.example.auctioncommon.domain.orders.dto.OrderResponse
import org.example.auctioncommon.domain.orders.service.OrderService
import org.example.auctioncommon.global.response.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/order")
class OrderController(
    private val orderService: OrderService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/me")
    fun findOrder(
        @AuthenticationPrincipal user: SecurityUser,
        @PageableDefault(size = 10, sort = ["orderId"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<OrderResponse>>> {
        val responses: Page<OrderResponse> = orderService.findOrder(user.userId, pageable)
        log.info("[Order] 사용자 {}의 낙찰 리스트 조회 요청 (page: {})", user.userId, pageable.pageNumber)
        return ResponseEntity.ok(ApiResponse.success("낙찰 리스트 조회", responses))
    }

    @GetMapping("/{auctionId}/auction")
    fun auctionOrder(@PathVariable auctionId: Long): ResponseEntity<ApiResponse<OrderResponse>> {
        val response: OrderResponse = orderService.auctionOrder(auctionId)
        log.info("[Auction] 옥션 {}의 낙찰 조회 요청", auctionId)
        return ResponseEntity.ok(ApiResponse.success("낙찰자 조회", response))
    }
}
