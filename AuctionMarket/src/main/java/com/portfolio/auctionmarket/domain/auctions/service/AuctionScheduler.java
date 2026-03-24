package com.portfolio.auctionmarket.domain.auctions.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionScheduler {

    private final AuctionService auctionService;
    private final RedissonClient redissonClient;

    @Scheduled(fixedDelay = 1000)
    public void checkExpiredAuctions() {
        RScoredSortedSet<Long> closingQueue = redissonClient.getScoredSortedSet("auction:closing");

        Long now = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();

        Collection<Long> expiredAuctionIds = closingQueue.pollFirst(
                closingQueue.count(Double.NEGATIVE_INFINITY, true, now, true)
        );

        if (expiredAuctionIds == null || expiredAuctionIds.isEmpty()) {
            return;
        }

        log.info("{}개의 경매가 종료되었습니다. 처리를 시작합니다.", expiredAuctionIds.size());

        for (Long auctionId : expiredAuctionIds) {
            try {
                auctionService.finishAuction(auctionId);
            } catch (Exception e) {
                log.error("경매 종료 처리 중 오류 발생 - ID: {}, 사유: {}", auctionId, e.getMessage());
            }
        }
    }
}
