package com.hongsy.ecommrsapi.product.repository;

import com.hongsy.ecommrsapi.product.entity.Product;
import static com.hongsy.ecommrsapi.product.entity.QProduct.product;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductCustomRepositoryImpl implements
    ProductCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Product> findProductsBySearchCondition() {
        return List.of();
    }

    @Override
    public List<Product> findProductsByKeyword(String keyword) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.or(product.name.contains(keyword));
        builder.or(product.brandName.contains(keyword));
        builder.or(product.info.contains(keyword));
        builder.or(product.colorGroup.contains(keyword));

        return jpaQueryFactory.selectFrom(product)
            .where(builder).fetch();
    }
}
