package com.portfolio.auctionmarket.domain.toss.service;

import com.portfolio.auctionmarket.domain.toss.dto.TossPaymentConfirmRequest;
import com.portfolio.auctionmarket.domain.toss.dto.TossPaymentConfirmResponse;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.domain.user.repository.UserRepository;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class TossPaymentService {
    private final WebClient tossWebClient;
    private final UserRepository userRepository;

    @Transactional
    public TossPaymentConfirmResponse confirm(TossPaymentConfirmRequest request, Long userId) {
        TossPaymentConfirmResponse response = tossWebClient.post()
                .uri("/payments/confirm")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(TossPaymentConfirmResponse.class)
                .block();

        if (response != null && "DONE".equals(response.getStatus())) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

            user.addPoint(response.getTotalAmount());
        } else {
            throw new CustomException(ErrorCode.PAYMENT_NOT_DONE);
        }

        return response;
    }

}
