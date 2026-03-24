package com.portfolio.auctionmarket.domain.auctions.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAuction is a Querydsl query type for Auction
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAuction extends EntityPathBase<Auction> {

    private static final long serialVersionUID = 1317280910L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAuction auction = new QAuction("auction");

    public final NumberPath<Long> auctionId = createNumber("auctionId", Long.class);

    public final ListPath<com.portfolio.auctionmarket.domain.bids.entity.Bid, com.portfolio.auctionmarket.domain.bids.entity.QBid> bids = this.<com.portfolio.auctionmarket.domain.bids.entity.Bid, com.portfolio.auctionmarket.domain.bids.entity.QBid>createList("bids", com.portfolio.auctionmarket.domain.bids.entity.Bid.class, com.portfolio.auctionmarket.domain.bids.entity.QBid.class, PathInits.DIRECT2);

    public final NumberPath<Long> currentPrice = createNumber("currentPrice", Long.class);

    public final DateTimePath<java.time.LocalDateTime> endTime = createDateTime("endTime", java.time.LocalDateTime.class);

    public final com.portfolio.auctionmarket.domain.products.entity.QProduct product;

    public final NumberPath<Long> startPrice = createNumber("startPrice", Long.class);

    public final DateTimePath<java.time.LocalDateTime> startTime = createDateTime("startTime", java.time.LocalDateTime.class);

    public final EnumPath<AuctionStatus> status = createEnum("status", AuctionStatus.class);

    public QAuction(String variable) {
        this(Auction.class, forVariable(variable), INITS);
    }

    public QAuction(Path<? extends Auction> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAuction(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAuction(PathMetadata metadata, PathInits inits) {
        this(Auction.class, metadata, inits);
    }

    public QAuction(Class<? extends Auction> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.product = inits.isInitialized("product") ? new com.portfolio.auctionmarket.domain.products.entity.QProduct(forProperty("product"), inits.get("product")) : null;
    }

}

