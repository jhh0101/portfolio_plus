package com.portfolio.auctionmarket.domain.toss.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TossPaymentConfirmResponse {
    private String paymentKey;
    private String orderId;
    private Long totalAmount;
    private String status;
    private String approvedAt;
}
