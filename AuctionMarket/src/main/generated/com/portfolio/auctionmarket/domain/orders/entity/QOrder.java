package com.portfolio.auctionmarket.domain.orders.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrder is a Querydsl query type for Order
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrder extends EntityPathBase<Order> {

    private static final long serialVersionUID = -1113289692L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrder order = new QOrder("order1");

    public final com.portfolio.auctionmarket.global.base.QBaseCreatedAt _super = new com.portfolio.auctionmarket.global.base.QBaseCreatedAt(this);

    public final com.portfolio.auctionmarket.domain.auctions.entity.QAuction auction;

    public final com.portfolio.auctionmarket.domain.user.entity.QUser buyer;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> finalPrice = createNumber("finalPrice", Long.class);

    public final NumberPath<Long> orderId = createNumber("orderId", Long.class);

    public QOrder(String variable) {
        this(Order.class, forVariable(variable), INITS);
    }

    public QOrder(Path<? extends Order> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrder(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrder(PathMetadata metadata, PathInits inits) {
        this(Order.class, metadata, inits);
    }

    public QOrder(Class<? extends Order> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.auction = inits.isInitialized("auction") ? new com.portfolio.auctionmarket.domain.auctions.entity.QAuction(forProperty("auction"), inits.get("auction")) : null;
        this.buyer = inits.isInitialized("buyer") ? new com.portfolio.auctionmarket.domain.user.entity.QUser(forProperty("buyer"), inits.get("buyer")) : null;
    }

}

