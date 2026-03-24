package com.portfolio.auctionmarket.domain.ratings.service;

import com.portfolio.auctionmarket.domain.orders.entity.Order;
import com.portfolio.auctionmarket.domain.orders.repository.OrderQueryRepository;
import com.portfolio.auctionmarket.domain.orders.repository.OrderRepository;
import com.portfolio.auctionmarket.domain.ratings.dto.RatingDeleteResponse;
import com.portfolio.auctionmarket.domain.ratings.dto.RatingRequest;
import com.portfolio.auctionmarket.domain.ratings.dto.RatingResponse;
import com.portfolio.auctionmarket.domain.ratings.entity.Rating;
import com.portfolio.auctionmarket.domain.ratings.entity.RatingStatus;
import com.portfolio.auctionmarket.domain.ratings.repository.RatingQueryRepository;
import com.portfolio.auctionmarket.domain.ratings.repository.RatingRepository;
import com.portfolio.auctionmarket.domain.user.entity.Role;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.domain.user.repository.UserRepository;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final RatingQueryRepository ratingQueryRepository;
    private final OrderQueryRepository orderQueryRepository;
    private final UserRepository userRepository;

    @Transactional
    public RatingResponse createRating(Long userId, Long orderId, RatingRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Order order = orderQueryRepository.findOrderWithProductAndSeller(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND, "주문 내역을 찾을 수 없습니다."));

        if (!order.getBuyer().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "구매자가 일치하지 않습니다.");
        }

        if (ratingRepository.existsByOrder(order)) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "이미 주문에 대한 평가가 존재합니다.");
        }

        Rating rating = Rating.builder()
                .order(order)
                .toUser(order.getSeller())
                .fromUser(user)
                .score(request.getScore())
                .comment(request.getComment())
                .status(RatingStatus.NORMAL)
                .build();

        Rating ratingSave = ratingRepository.save(rating);

        Double ratingAvg = ratingQueryRepository.avgRating(ratingSave.getToUser().getUserId());
        ratingSave.getToUser().updateRating(ratingAvg);

        return RatingResponse.from(ratingSave);
    }

    @Transactional(readOnly = true)
    public RatingResponse findRating(Long orderId) {
        Rating rating = ratingRepository.findByOrder_OrderId(orderId);
        if(rating == null) {
            return null;
        }
        return RatingResponse.from(rating);
    }

    @Transactional
    public RatingResponse updateRating(Long userId, Long orderId, Long ratingId, RatingRequest request) {
        Rating rating = ratingQueryRepository.findById(ratingId)
                .orElseThrow(() -> new CustomException(ErrorCode.RATING_NOT_FOUND, "평가를 찾을 수 없습니다."));

        if (!rating.getFromUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "작성자가 일치하지 않습니다.");
        }

        if (!rating.getOrder().getOrderId().equals(orderId)) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "잘못된 주문 경로입니다.");
        }

        rating.updateRating(request);

        Double ratingAvg = ratingQueryRepository.avgRating(rating.getToUser().getUserId());
        rating.getToUser().updateRating(ratingAvg);

        return RatingResponse.from(rating);
    }

    @Transactional
    public RatingDeleteResponse deleteRating(Long userId, Long ratingId) {
        Rating rating = ratingQueryRepository.findById(ratingId)
                .orElseThrow(() -> new CustomException(ErrorCode.RATING_NOT_FOUND, "평가를 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        if (!rating.getFromUser().getUserId().equals(userId) && !user.getRole().equals(Role.ADMIN)) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "삭제 권한이 없습니다.");
        }

        ratingRepository.delete(rating);

        Double ratingAvg = ratingQueryRepository.avgRating(rating.getToUser().getUserId());
        rating.getToUser().updateRating(ratingAvg);

        return RatingDeleteResponse.from(rating);
    }

    @Transactional(readOnly = true)
    public Page<RatingResponse> findRatingList(Long toUserId, Pageable pageable) {
        Page<Rating> ratings = ratingRepository.findAllByToUser_UserId(toUserId, pageable);
        return ratings.map(RatingResponse::from);
    }
}
