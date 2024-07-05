package com.example.core.domain.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -469433826L;

    public static final QUser user = new QUser("user");

    public final com.example.core.domain.common.QBaseEntity _super = new com.example.core.domain.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final ListPath<com.example.core.domain.order.domain.Order, com.example.core.domain.order.domain.QOrder> orders = this.<com.example.core.domain.order.domain.Order, com.example.core.domain.order.domain.QOrder>createList("orders", com.example.core.domain.order.domain.Order.class, com.example.core.domain.order.domain.QOrder.class, PathInits.DIRECT2);

    public final StringPath password = createString("password");

    public final EnumPath<UserRole> role = createEnum("role", UserRole.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

