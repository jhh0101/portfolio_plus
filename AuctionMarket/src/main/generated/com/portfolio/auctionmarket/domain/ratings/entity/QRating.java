package com.portfolio.auctionmarket.domain.ratings.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRating is a Querydsl query type for Rating
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRating extends EntityPathBase<Rating> {

    private static final long serialVersionUID = 1254572296L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRating rating = new QRating("rating");

    public final com.portfolio.auctionmarket.global.base.QBaseCreatedAt _super = new com.portfolio.auctionmarket.global.base.QBaseCreatedAt(this);

    public final StringPath comment = createString("comment");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final com.portfolio.auctionmarket.domain.user.entity.QUser fromUser;

    public final com.portfolio.auctionmarket.domain.orders.entity.QOrder order;

    public final NumberPath<Long> ratingId = createNumber("ratingId", Long.class);

    public final NumberPath<Integer> score = createNumber("score", Integer.class);

    public final EnumPath<RatingStatus> status = createEnum("status", RatingStatus.class);

    public final com.portfolio.auctionmarket.domain.user.entity.QUser toUser;

    public QRating(String variable) {
        this(Rating.class, forVariable(variable), INITS);
    }

    public QRating(Path<? extends Rating> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRating(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRating(PathMetadata metadata, PathInits inits) {
        this(Rating.class, metadata, inits);
    }

    public QRating(Class<? extends Rating> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.fromUser = inits.isInitialized("fromUser") ? new com.portfolio.auctionmarket.domain.user.entity.QUser(forProperty("fromUser"), inits.get("fromUser")) : null;
        this.order = inits.isInitialized("order") ? new com.portfolio.auctionmarket.domain.orders.entity.QOrder(forProperty("order"), inits.get("order")) : null;
        this.toUser = inits.isInitialized("toUser") ? new com.portfolio.auctionmarket.domain.user.entity.QUser(forProperty("toUser"), inits.get("toUser")) : null;
    }

}

