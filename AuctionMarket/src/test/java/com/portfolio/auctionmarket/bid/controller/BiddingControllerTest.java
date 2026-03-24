package com.portfolio.auctionmarket.bid.controller;

import com.portfolio.auctionmarket.auth.dto.SecurityUser;
import com.portfolio.auctionmarket.auth.service.JwtService;
import com.portfolio.auctionmarket.domain.auctions.entity.Auction;
import com.portfolio.auctionmarket.domain.auctions.entity.AuctionStatus;
import com.portfolio.auctionmarket.domain.bids.controller.BidController;
import com.portfolio.auctionmarket.domain.bids.dto.BidRequest;
import com.portfolio.auctionmarket.domain.bids.dto.BidResultResponse;
import com.portfolio.auctionmarket.domain.bids.service.BidService;
import com.portfolio.auctionmarket.domain.categories.entity.Category;
import com.portfolio.auctionmarket.domain.products.entity.Product;
import com.portfolio.auctionmarket.domain.products.entity.ProductStatus;
import com.portfolio.auctionmarket.domain.user.entity.Role;
import com.portfolio.auctionmarket.domain.user.entity.User;
import com.portfolio.auctionmarket.domain.user.entity.UserStatus;
import com.portfolio.auctionmarket.global.error.CustomException;
import com.portfolio.auctionmarket.global.error.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BidController.class)
public class BiddingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BidService bidService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private SecurityUser securityUser;

    private User testUser1;
    private User testUser2;
    private Product testProduct;
    private Auction testAuction;
    private Category testCategory;

    @BeforeEach
    void setUp() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        testCategory = Category.builder()
                .category("testCategory")
                .parent(null)
                .build();

        testUser1 = User.builder()
                .userId(1L)
                .email("test1@test.com")
                .password("test!@#")
                .username("test1")
                .nickname("test1")
                .phone("01011111111")
                .role(Role.SELLER)
                .baseAddress("123")
                .detailAddress("456")
                .point(1000000L)
                .avgRating(0.0)
                .status(UserStatus.NORMAL)
                .build();

        testUser2 = User.builder()
                .userId(2L)
                .email("test2@test.com")
                .password("test!@#")
                .username("test2")
                .nickname("test2")
                .phone("01022222222")
                .role(Role.USER)
                .baseAddress("123")
                .detailAddress("456")
                .point(1000000L)
                .avgRating(0.0)
                .status(UserStatus.NORMAL)
                .build();

        testProduct = Product.builder()
                .productId(1L)
                .seller(testUser1)
                .category(testCategory)
                .title("testProduct")
                .description("testProductDesc")
                .viewCount(0)
                .productStatus(ProductStatus.ACTIVE)
                .image(new ArrayList<>())
                .build();

        testAuction = Auction.builder()
                .auctionId(1L)
                .product(testProduct)
                .startPrice(10000L)
                .currentPrice(10000L)
                .startTime(now)
                .endTime(now.plusDays(5))
                .status(AuctionStatus.PROCEEDING)
                .build();

        securityUser = new SecurityUser(testUser2);
    }

    @Test
    void bidding() throws Exception {

        BidRequest request = new BidRequest(15000L);

        String body = objectMapper.writeValueAsString(request);

        BidResultResponse mockResponse = BidResultResponse.builder()
                .auctionId(testAuction.getAuctionId())
                .price(15000L)
                .currentPoint(testUser2.getPoint() - 15000L)
                .title(testAuction.getProduct().getTitle())
                .build();

        given(bidService.addBid(anyLong(), anyLong(), any(BidRequest.class)))
                .willReturn(mockResponse);

        mockMvc.perform(post("/api/auction/{auctionId}", testAuction.getAuctionId())
                        .with(user(securityUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        then(bidService).should(times(1))
                .addBid(eq(securityUser.getUser().getUserId()),
                        eq(testAuction.getAuctionId()),
                        any(BidRequest.class));
    }

    @Test
    void bidding_nullPrice_validationFail() throws Exception {
        BidRequest request = new BidRequest(null);

        String body = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/auction/{auctionId}", testAuction.getAuctionId())
                        .with(user(securityUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()))
                .andExpect(jsonPath("$.message").exists());

        then(bidService).should(never()).addBid(anyLong(), anyLong(), any());
    }

    @Test
    void bidding_userNotFound() throws Exception {
        BidRequest request = new BidRequest(15000L);

        String body = objectMapper.writeValueAsString(request);

        willThrow(new CustomException(ErrorCode.USER_NOT_FOUND))
                .given(bidService).addBid(anyLong(), eq(testAuction.getAuctionId()), any(BidRequest.class));

        mockMvc.perform(post("/api/auction/{auctionId}", testAuction.getAuctionId())
                        .with(user(securityUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()));
    }

    @Test
    void bidding_notEnoughPoints() throws Exception {
        BidRequest request = new BidRequest(15000L);

        String body = objectMapper.writeValueAsString(request);

        willThrow(new CustomException(ErrorCode.NOT_ENOUGH_POINTS))
                .given(bidService).addBid(anyLong(), eq(testAuction.getAuctionId()), any(BidRequest.class));

        mockMvc.perform(post("/api/auction/{auctionId}", testAuction.getAuctionId())
                        .with(user(securityUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(jsonPath("$.code").value(ErrorCode.NOT_ENOUGH_POINTS.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.NOT_ENOUGH_POINTS.getMessage()));
    }

    @Test
    void bidding_auctionNotFound() throws Exception {
        BidRequest request = new BidRequest(15000L);

        String body = objectMapper.writeValueAsString(request);

        willThrow(new CustomException(ErrorCode.AUCTION_NOT_FOUND))
                .given(bidService).addBid(anyLong(), eq(testAuction.getAuctionId()), any(BidRequest.class));

        mockMvc.perform(post("/api/auction/{auctionId}", testAuction.getAuctionId())
                        .with(user(securityUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(jsonPath("$.code").value(ErrorCode.AUCTION_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.AUCTION_NOT_FOUND.getMessage()));
    }

    @Test
    void bidding_invalidAuctionTime() throws Exception {
        BidRequest request = new BidRequest(15000L);

        String body = objectMapper.writeValueAsString(request);

        willThrow(new CustomException(ErrorCode.INVALID_AUCTION_TIME))
                .given(bidService).addBid(anyLong(), eq(testAuction.getAuctionId()), any(BidRequest.class));

        mockMvc.perform(post("/api/auction/{auctionId}", testAuction.getAuctionId())
                        .with(user(securityUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_AUCTION_TIME.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_AUCTION_TIME.getMessage()));
    }

    @Test
    void bidding_selfBidNotAllowed() throws Exception {
        BidRequest request = new BidRequest(15000L);

        String body = objectMapper.writeValueAsString(request);

        willThrow(new CustomException(ErrorCode.SELF_BID_NOT_ALLOWED))
                .given(bidService).addBid(anyLong(), eq(testAuction.getAuctionId()), any(BidRequest.class));

        mockMvc.perform(post("/api/auction/{auctionId}", testAuction.getAuctionId())
                        .with(user(securityUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(jsonPath("$.code").value(ErrorCode.SELF_BID_NOT_ALLOWED.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.SELF_BID_NOT_ALLOWED.getMessage()));
    }

    @Test
    void bidding_bidPriceTooLow() throws Exception {
        BidRequest request = new BidRequest(15000L);

        String body = objectMapper.writeValueAsString(request);

        willThrow(new CustomException(ErrorCode.BID_PRICE_TOO_LOW))
                .given(bidService).addBid(anyLong(), eq(testAuction.getAuctionId()), any(BidRequest.class));

        mockMvc.perform(post("/api/auction/{auctionId}", testAuction.getAuctionId())
                        .with(user(securityUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(jsonPath("$.code").value(ErrorCode.BID_PRICE_TOO_LOW.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.BID_PRICE_TOO_LOW.getMessage()));
    }

    @Test
    void bidding_alreadyHighestBidder() throws Exception {
        BidRequest request = new BidRequest(15000L);

        String body = objectMapper.writeValueAsString(request);

        willThrow(new CustomException(ErrorCode.ALREADY_HIGHEST_BIDDER))
                .given(bidService).addBid(anyLong(), eq(testAuction.getAuctionId()), any(BidRequest.class));

        mockMvc.perform(post("/api/auction/{auctionId}", testAuction.getAuctionId())
                        .with(user(securityUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(result -> {
                    System.out.println("TEST 결과 : ");
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andDo(print())
                .andExpect(jsonPath("$.code").value(ErrorCode.ALREADY_HIGHEST_BIDDER.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.ALREADY_HIGHEST_BIDDER.getMessage()));
    }
}
