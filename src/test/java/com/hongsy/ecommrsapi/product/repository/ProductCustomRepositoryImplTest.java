package com.hongsy.ecommrsapi.product.repository;

import com.hongsy.ecommrsapi.util.FullIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ProductCustomRepositoryImplTest extends FullIntegrationTest {


    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("재고 필터링: 재고가 0보다 큰 상품만 조회")
    void findProductsBySearchCondition_StockGtZero() {

    }

    @Test
    @DisplayName("가격 범위 검색: 지정된 가격 범위 내의 상품만 조회")
    void findProductsBySearchCondition_PriceRange() {

    }

    @Test
    @DisplayName("키워드 검색: 이름, 브랜드, 정보, 색상 중 하나라도 키워드를 포함하면 조회")
    void findProductsBySearchCondition_Keyword() {

    }

    @Test
    @DisplayName("복합 조건 검색: 가격과 키워드 조건을 모두 만족하는 상품만 조회")
    void findProductsBySearchCondition_Complex() {

    }
}