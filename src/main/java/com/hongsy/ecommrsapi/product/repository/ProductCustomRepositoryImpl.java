package com.hongsy.ecommrsapi.product.repository;

import static com.hongsy.ecommrsapi.product.entity.QProduct.product;

import com.hongsy.ecommrsapi.product.dto.SearchRequestDto;
import com.hongsy.ecommrsapi.product.entity.Product;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class ProductCustomRepositoryImpl implements
    ProductCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Product> findProductsBySearchCondition(SearchRequestDto requestDto) {
        BooleanBuilder builder = new BooleanBuilder();

        // 재고 0 이상
        builder.and(product.stockQuantity.gt(0));

        // 가격 범위 조건
        if(requestDto.getMinPrice()!=null&&requestDto.getMaxPrice()!=null){
            builder.and(product.price.between(requestDto.getMinPrice(),requestDto.getMaxPrice()));
        }

        // 키워드 포함
        if(StringUtils.hasText(requestDto.getKeyword())){
            BooleanBuilder keywordOr = new BooleanBuilder();
            keywordOr.or(product.name.containsIgnoreCase(requestDto.getKeyword()));
            keywordOr.or(product.brandName.containsIgnoreCase(requestDto.getKeyword()));
            keywordOr.or(product.info.containsIgnoreCase(requestDto.getKeyword()));
            keywordOr.or(product.colorGroup.containsIgnoreCase(requestDto.getKeyword()));
            keywordOr.or(Expressions.booleanTemplate(
                "function('jsonb_exists', {0}, {1}) = true",
                product.tags,
                requestDto.getKeyword()
            ));

            builder.and(keywordOr);
        }

        return jpaQueryFactory.selectFrom(product).where(builder).fetch();
    }

}
