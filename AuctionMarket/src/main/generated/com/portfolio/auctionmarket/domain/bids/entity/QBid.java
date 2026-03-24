package com.portfolio.auctionmarket.domain.bids.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBid is a Querydsl query type for Bid
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBid extends EntityPathBase<Bid> {

    private static final long serialVersionUID = -542843774L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBid bid = new QBid("bid");

    public final com.portfolio.auctionmarket.global.base.QBaseCreatedAt _super = new com.portfolio.auctionmarket.global.base.QBaseCreatedAt(this);

    public final com.portfolio.auctionmarket.domain.auctions.entity.QAuction auction;

    public final com.portfolio.auctionmarket.domain.user.entity.QUser bidder;

    public final NumberPath<Long> bidId = createNumber("bidId", Long.class);

    public final NumberPath<Long> bidPrice = createNumber("bidPrice", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final EnumPath<BidStatus> status = createEnum("status", BidStatus.class);

    public QBid(String variable) {
        this(Bid.class, forVariable(variable), INITS);
    }

    public QBid(Path<? extends Bid> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBid(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBid(PathMetadata metadata, PathInits inits) {
        this(Bid.class, metadata, inits);
    }

    public QBid(Class<? extends Bid> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.auction = inits.isInitialized("auction") ? new com.portfolio.auctionmarket.domain.auctions.entity.QAuction(forProperty("auction"), inits.get("auction")) : null;
        this.bidder = inits.isInitialized("bidder") ? new com.portfolio.auctionmarket.domain.user.entity.QUser(forProperty("bidder"), inits.get("bidder")) : null;
    }

}

