package com.portfolio.auctionmarket.domain.orders.service;

import com.portfolio.auctionmarket.domain.orders.dto.OrderResponse;
import com.portfolio.auctionmarket.domain.orders.entity.Order;
import com.portfolio.auctionmarket.domain.orders.repository.OrderRepository;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderAdminService {
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public Slice<OrderResponse> findOrder(Long userId, Pageable pageable) {
        Slice<Order> orders = orderRepository.findAllByBuyer_UserId(userId, pageable);
        log.info("사용자 {}의 낙찰 내역 조회 요청", userId);
        return orders.map(OrderResponse::from);
    }

}
