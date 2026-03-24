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
import com.portfolio.auctionmarket.domain.ratings.entity.Rating;
import com.portfolio.auctionmarket.domain.ratings.repository.RatingRepository;
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
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final ProductQueryRepository productQueryRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final S3Service s3Service;
    private final RedissonClient redissonClient;

    // 상품 메서드
    @Transactional
    public ProductResponse addProduct(Long userId, ProductRequest productRequest, AuctionRequest auctionRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Product product = Product.builder()
                .seller(user)
                .category(category)
                .title(productRequest.getTitle())
                .description(productRequest.getDescription())
                .viewCount(0)
                .productStatus(ProductStatus.ACTIVE)
                .image(new ArrayList<>())
                .build();

        Product productSave = productRepository.save(product);

        Auction auction = Auction.builder()
                .product(productSave)
                .startPrice(auctionRequest.getStartPrice())
                .currentPrice(auctionRequest.getStartPrice())
                .startTime(auctionRequest.getStartTime())
                .endTime(auctionRequest.getEndTime())
                .status(AuctionStatus.PROCEEDING)
                .build();

        Auction auctionSave = auctionRepository.save(auction);


        RScoredSortedSet<Long> closingQueue = redissonClient.getScoredSortedSet("auction:closing");

        Long closingTimestamp = auctionSave.getEndTime()
                .atZone(ZoneId.systemDefault())
                .toEpochSecond();

        closingQueue.add(closingTimestamp, auctionSave.getAuctionId());

        return ProductResponse.from(productSave);

    }

    @Transactional(readOnly = true)
    public Page<ProductAndAuctionResponse> productList(ProductListCondition condition, Pageable pageable) {
        Page<Product> auctions = productQueryRepository.productList(null, condition, pageable);

        return auctions.map(ProductAndAuctionResponse::from);
    }

    @Transactional
    public ProductDetailAndAuctionResponse findProductDetail(Long productId, HttpServletRequest request, HttpServletResponse response, Long userId) {

        Product product = productRepository.findWithAuctionById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND, "상품을 찾을 수 없습니다."));

        User user = userRepository.findById(product.getSeller().getUserId()).orElse(null);

        Cookie[] cookies = request.getCookies();
        boolean isViewed = false;
        Cookie viewCookie = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("viewed_products")) {
                    viewCookie = cookie;
                    if (cookie.getValue().contains("[" + productId + "]")) {
                        isViewed = true;
                    }
                    break;
                }
            }
        }

        boolean isNotSeller = (userId == null) || !product.getSeller().getUserId().equals(userId);

        if (!isViewed && isNotSeller) {
            productRepository.viewCount(productId);

            if (viewCookie != null) {
                viewCookie.setValue(viewCookie.getValue() + "_[" + productId + "]");
            } else {
                viewCookie = new Cookie("viewed_products", "[" + productId + "]");
            }

            viewCookie.setPath("/");
            viewCookie.setMaxAge(60 * 60 * 24);
            viewCookie.setHttpOnly(true);
            response.addCookie(viewCookie);
        }


        return ProductDetailAndAuctionResponse.from(product, user);
    }

    @Transactional(readOnly = true)
    public Page<ProductAndAuctionResponse> myProductList(Long userId, ProductListCondition condition, Pageable pageable) {
        if (userId == null) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN, "토큰이 만료되었습니다.");
        }

        Page<Product> auctions = productQueryRepository.productList(userId, condition, pageable);

        return auctions.map(ProductAndAuctionResponse::from);
    }

    @Transactional
    public ProductDetailAndAuctionResponse updateProductDetail(Long userId, Long productId, ProductRequest productRequest, AuctionRequest auctionRequest) {

        Product product = productRepository.findWithAuctionById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND, "상품을 찾을 수 없습니다."));

        if (!userId.equals(product.getSeller().getUserId())) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "사용자가 일치하지 않습니다.");
        }

        if (bidRepository.existsByStatusAndAuction(BidStatus.ACTIVE, product.getAuction())) {
            throw new CustomException(ErrorCode.CANNOT_MODIFY_AFTER_BID, "입찰한 상품은 수정할 수 없습니다.");
        }

        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        product.updateProduct(
                category,
                productRequest.getTitle(),
                productRequest.getDescription(),
                auctionRequest.getStartPrice(),
                auctionRequest.getStartTime(),
                auctionRequest.getEndTime()
        );

        RScoredSortedSet<Long> closingQueue = redissonClient.getScoredSortedSet("auction:closing");

        long newScore = auctionRequest.getEndTime().atZone(ZoneId.systemDefault()).toEpochSecond();

        closingQueue.add(newScore, product.getAuction().getAuctionId());

        return ProductDetailAndAuctionResponse.from(product);
    }

    // 상품 삭제
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

    // 이미지 메서드
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
