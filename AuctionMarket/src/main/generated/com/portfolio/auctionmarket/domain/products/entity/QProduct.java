package com.portfolio.auctionmarket.domain.products.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProduct is a Querydsl query type for Product
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProduct extends EntityPathBase<Product> {

    private static final long serialVersionUID = -1760878106L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProduct product = new QProduct("product");

    public final com.portfolio.auctionmarket.global.base.QBaseCreatedAt _super = new com.portfolio.auctionmarket.global.base.QBaseCreatedAt(this);

    public final com.portfolio.auctionmarket.domain.auctions.entity.QAuction auction;

    public final com.portfolio.auctionmarket.domain.categories.entity.QCategory category;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath description = createString("description");

    public final ListPath<ProductImage, QProductImage> image = this.<ProductImage, QProductImage>createList("image", ProductImage.class, QProductImage.class, PathInits.DIRECT2);

    public final NumberPath<Long> productId = createNumber("productId", Long.class);

    public final EnumPath<ProductStatus> productStatus = createEnum("productStatus", ProductStatus.class);

    public final com.portfolio.auctionmarket.domain.user.entity.QUser seller;

    public final StringPath title = createString("title");

    public final NumberPath<Integer> viewCount = createNumber("viewCount", Integer.class);

    public QProduct(String variable) {
        this(Product.class, forVariable(variable), INITS);
    }

    public QProduct(Path<? extends Product> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProduct(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProduct(PathMetadata metadata, PathInits inits) {
        this(Product.class, metadata, inits);
    }

    public QProduct(Class<? extends Product> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.auction = inits.isInitialized("auction") ? new com.portfolio.auctionmarket.domain.auctions.entity.QAuction(forProperty("auction"), inits.get("auction")) : null;
        this.category = inits.isInitialized("category") ? new com.portfolio.auctionmarket.domain.categories.entity.QCategory(forProperty("category"), inits.get("category")) : null;
        this.seller = inits.isInitialized("seller") ? new com.portfolio.auctionmarket.domain.user.entity.QUser(forProperty("seller"), inits.get("seller")) : null;
    }

}

