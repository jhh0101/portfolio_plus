package com.portfolio.auctionmarket.domain.products.service;

import com.portfolio.auctionmarket.domain.auctions.dto.AuctionRequest;
import com.portfolio.auctionmarket.domain.auctions.entity.Auction;
import com.portfolio.auctionmarket.domain.auctions.entity.AuctionStatus;
import com.portfolio.auctionmarket.domain.auctions.repository.AuctionRepository;
import com.portfolio.auctionmarket.domain.bids.entity.BidStatus;
import com.portfolio.auctionmarket.domain.bids.repository.BidRepository;
import com.portfolio.auctionmarket.domain.categories.entity.Category;
import com.portfolio.auctionmarket.domain.categories.repository.CategoryRepository;
import com.portfolio.auctionmarket.domain.products.dto.*;
import com.portfolio.auctionmarket.domain.products.entity.Product;
import com.portfolio.auctionmarket.domain.products.entity.ProductImage;
import com.portfolio.auctionmarket.domain.products.entity.ProductStatus;
import com.portfolio.auctionmarket.domain.products.repository.ProductImageRepository;
import com.portfolio.auctionmarket.domain.products.repository.ProductQueryRepository;
import com.portfolio.auctionmarket.domain.products.repository.ProductRepository;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.domain.user.repository.UserRepository;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import com.portfolio.auctionmarket.global.s3.service.S3Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductAdminService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final ProductQueryRepository productQueryRepository;
    private final BidRepository bidRepository;
    private final S3Service s3Service;
    private final RedissonClient redissonClient;

    @Transactional(readOnly = true)
    public Slice<ProductAndAuctionResponse> userProductList(Long userId, ProductListCondition condition, Pageable pageable) {
        Slice<Product> auctions = productQueryRepository.adminProductList(userId, condition, pageable);

        return auctions.map(ProductAndAuctionResponse::from);
    }

    @Transactional
    public void deleteProduct(Long userId, Long productId) {

        Product product = productRepository.findWithAuctionById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND, "상품을 찾을 수 없습니다."));

        if (!userId.equals(product.getSeller().getUserId())) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "사용자가 일치하지 않습니다.");
        }

        if (bidRepository.existsByStatusAndAuction(BidStatus.ACTIVE, product.getAuction())) {
            throw new CustomException(ErrorCode.CANNOT_DELETE_AFTER_BID, "입찰한 상품은 삭제할 수 없습니다.");
        }

        for (ProductImage img : product.getImage()) {
            s3Service.deleteFile(img.getImageUrl());
        }

        product.getAuction().changeStatus(AuctionStatus.CANCELED);
        productRepository.delete(product);

        Long auctionId = product.getAuction().getAuctionId();
        RScoredSortedSet<Long> closingQueue = redissonClient.getScoredSortedSet("auction:closing");
        boolean removed = closingQueue.remove(auctionId);

        if (removed) {
            log.info("경매 삭제로 인한 Redis 스케줄 제거 완료 - Auction ID: {}", auctionId);
        }
    }

    @Transactional
    public List<ProductImageResponse> uploadImages(Long productId, List<MultipartFile> files) {
        List<ProductImageResponse> responses = new ArrayList<>();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND, "상품을 찾을 수 없습니다."));

        Integer lastOrder = productImageRepository.findMaxOrderByProductId(productId);

        for (int i = 0; i < files.size(); i++) {
            int order = lastOrder + i + 1; // 0번 인덱스는 1번, 1번 인덱스는 2번...
            String url = s3Service.uploadFile(files.get(i), "products");

            ProductImage img = ProductImage.builder()
                    .product(product)
                    .imageUrl(url)
                    .imageOrder(order) // 여기서 상품별 순번 결정!
                    .build();

            ProductImage savedImg = productImageRepository.save(img);

            // 생성된 정보를 DTO에 담아 리스트에 추가
            responses.add(ProductImageResponse.from(savedImg));
        }
        return responses;
    }

    @Transactional(readOnly = true)
    public List<ProductImageResponse> loadImage(Long productId) {
        List<ProductImage> productImages = productImageRepository.findByProduct_ProductIdOrderByImageOrderAsc(productId);
        return productImages.stream().map(ProductImageResponse::from).toList();
    }

    @Transactional
    public void moveToMain(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));

        Integer oldOrder = image.getImageOrder();
        Integer newOrder = 1;

        productImageRepository.shiftOrders(image.getProduct().getProductId(), newOrder, oldOrder);

        image.updateOrder(newOrder);
    }

    @Transactional
    public void deleteImage(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));
        s3Service.deleteFile(image.getImageUrl());
        productImageRepository.delete(image);

        List<ProductImage> remainingImages = productImageRepository.findByProduct_ProductIdOrderByImageOrderAsc(image.getProduct().getProductId());

        for (int i = 0; i < remainingImages.size(); i++) {
            remainingImages.get(i).updateOrder(i + 1); // 엔티티에 updateOrder 메서드 필요
        }
    }
}
