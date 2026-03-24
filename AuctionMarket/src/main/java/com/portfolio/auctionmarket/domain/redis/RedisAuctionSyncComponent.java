package com.portfolio.auctionmarket.domain.redis;

import com.portfolio.auctionmarket.domain.auctions.entity.AuctionStatus;
import com.portfolio.auctionmarket.domain.auctions.repository.AuctionRepository;
import com.portfolio.auctionmarket.domain.products.entity.Product;
import com.portfolio.auctionmarket.domain.products.entity.ProductStatus;
import com.portfolio.auctionmarket.domain.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBatch;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisAuctionSyncComponent {
    private final RedissonClient redissonClient;
    private final ProductRepository productRepository;
    private final AuctionRepository auctionRepository;

    @Order(1)
    @EventListener(ApplicationReadyEvent.class)
    public void updateExpiredAuctions() {
        LocalDateTime now = LocalDateTime.now();
        productRepository.updateProductStatusSold(now, ProductStatus.SOLD, ProductStatus.ACTIVE, AuctionStatus.PROCEEDING);
        productRepository.updateProductStatusFailed(now, ProductStatus.FAILED, ProductStatus.ACTIVE, AuctionStatus.PROCEEDING);
        auctionRepository.updateAuctionEnded(now, AuctionStatus.ENDED, AuctionStatus.PROCEEDING);
        log.info("경매 DB 벌크 업데이트 완료");
    }

    @Order(2)
    @EventListener(ApplicationReadyEvent.class)
    @Transactional(readOnly = true)
    public void syncExistingDataToRedis() {

        RBatch batch = redissonClient.createBatch();
        int processCount = 0;

        try (Stream<Product> productList = productRepository.findAllByProductStatus(ProductStatus.ACTIVE)){
            for (Product product : (Iterable<Product>) productList::iterator){
                long closingTimestamp = product.getAuction().getEndTime()
                        .atZone(ZoneId.systemDefault())
                        .toEpochSecond();
                batch.getScoredSortedSet("auction:closing")
                        .addAsync(closingTimestamp, product.getAuction().getAuctionId());

                processCount++;
                int batchSize = 500;
                if (processCount % batchSize == 0) {
                    batch.execute();
                    batch = redissonClient.createBatch();
                }
            }
        }
        batch.execute();
        log.info("{}건 동기화 완료 : ", processCount);
    }
}
