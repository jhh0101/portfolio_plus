package com.portfolio.auctionmarket.global.base;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBaseCreatedAt is a Querydsl query type for BaseCreatedAt
 */
@Generated("com.querydsl.codegen.DefaultSupertypeSerializer")
public class QBaseCreatedAt extends EntityPathBase<BaseCreatedAt> {

    private static final long serialVersionUID = -293700010L;

    public static final QBaseCreatedAt baseCreatedAt = new QBaseCreatedAt("baseCreatedAt");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public QBaseCreatedAt(String variable) {
        super(BaseCreatedAt.class, forVariable(variable));
    }

    public QBaseCreatedAt(Path<? extends BaseCreatedAt> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBaseCreatedAt(PathMetadata metadata) {
        super(BaseCreatedAt.class, metadata);
    }

}

