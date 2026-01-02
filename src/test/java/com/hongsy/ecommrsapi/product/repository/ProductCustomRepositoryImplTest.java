package com.hongsy.ecommrsapi.product.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.hongsy.ecommrsapi.product.dto.SearchRequestDto;
import com.hongsy.ecommrsapi.product.entity.Product;
import com.hongsy.ecommrsapi.user.entity.Gender;
import com.hongsy.ecommrsapi.user.entity.Role;
import com.hongsy.ecommrsapi.user.entity.User;
import com.hongsy.ecommrsapi.user.entity.UserStatus;
import com.hongsy.ecommrsapi.util.FullIntegrationTest;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ProductCustomRepositoryImplTest extends FullIntegrationTest {


    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager em;

    private User seller;

    @BeforeEach
    void setUp() {
        seller = User.builder()
            .email("test@test.com")
            .password("testpassword123")
            .name("tester")
            .age(30)
            .gender(Gender.Male)
            .phoneNumber("010-1234-5678")
            .address("경기도 수원시")
            .roles(Set.of(Role.ROLE_SELLER))
            .status(UserStatus.ACTIVE).build();
        em.persist(seller);
    }

    private Product createProduct(String name, String brandName, String info, Long price,
        String colorGroup, List<String> tags, Long orderAmountFor30d, Integer stockQuantity,
        Double avgReviewScore, Integer likeCount) {
        Product product = Product.builder()
            .name(name)
            .brandName(brandName)
            .info(info)
            .price(BigDecimal.valueOf(price))
            .colorGroup(colorGroup)
            .tags(tags)
            .orderAmountFor30d(orderAmountFor30d)
            .stockQuantity(stockQuantity)
            .avgReviewScore(avgReviewScore)
            .sellerId(seller.getId())
            .seller(seller)
            .likeCount(likeCount)
            .build();
        return productRepository.save(product);
    }

    @Test
    @DisplayName("재고 필터링: 재고가 0보다 큰 상품만 조회")
    void findProductsBySearchCondition_StockGtZero() {
        createProduct("상품A", "브랜드A", "재고있음", 30000L, "어두운", List.of("태그1", "태그2"), 30L, 300, 4.0,
            4);
        createProduct("상품B", "브랜드B", "재고없음", 30000L, "어두운", List.of("태그1", "태그2"), 30L, 0, 4.0, 4);

        SearchRequestDto requestDto = SearchRequestDto.builder().build();
        List<Product> products = productRepository.findProductsBySearchCondition(requestDto);

        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("상품A");
    }

    @Test
    @DisplayName("가격 범위 검색: 지정된 가격 범위 내의 상품만 조회")
    void findProductsBySearchCondition_PriceRange() {
        createProduct("상품A", "브랜드A", "재고있음", 30000L, "어두운", List.of("태그1", "태그2"), 30L, 300, 4.0,
            4);
        createProduct("상품B", "브랜드B", "재고있음", 50000L, "어두운", List.of("태그1", "태그2"), 30L, 300, 4.0,
            4);

        SearchRequestDto requestDto = SearchRequestDto.builder()
            .minPrice(BigDecimal.valueOf(20000L)).maxPrice(BigDecimal.valueOf(40000L)).build();
        List<Product> products = productRepository.findProductsBySearchCondition(requestDto);

        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("상품A");
    }

    @Test
    @DisplayName("키워드 검색: 이름, 브랜드, 정보, 색상 중 하나라도 키워드를 포함하면 조회")
    void findProductsBySearchCondition_Keyword() {
        createProduct("상품A", "브랜드A", "재고있음", 30000L, "어두운", List.of("태그1", "태그2"), 30L, 300, 4.0,
            4);
        createProduct("상품B", "브랜드B", "재고있음", 50000L, "어두운", List.of("태그3", "태그4"), 30L, 300, 4.0,
            4);

        SearchRequestDto requestDto = SearchRequestDto.builder().keyword("태그1").build();
        List<Product> products = productRepository.findProductsBySearchCondition(requestDto);

        assertThat(products).hasSize(1);
    }

    @Test
    @DisplayName("정렬 조건 검색 : 주문량 많은순, 평점 높은순, 좋아요 많은순, 가격 낮은순, 가격 높은순 등 정렬 조건 추가하여 조회")
    void findProductsBySearchCondition_SortBy(){
        createProduct("상품A", "브랜드A", "재고있음", 30000L, "어두운", List.of("태그1", "태그2"), 10L, 300, 3.0,
            4);
        createProduct("상품B", "브랜드B", "재고있음", 50000L, "어두운", List.of("태그3", "태그4"), 30L, 300, 4.0,
            1);

        SearchRequestDto requestDto = SearchRequestDto.builder().sortType("sales").build();
        List<Product> products = productRepository.findProductsBySearchCondition(requestDto);

        assertThat(products).hasSize(2);
        assertThat(products.get(0).getName()).isEqualTo("상품B");
    }

    @Test
    @DisplayName("복합 조건 검색: 가격과 키워드 정렬 조건을 모두 만족하는 상품만 조회")
    void findProductsBySearchCondition_Complex() {
        createProduct("상품A", "브랜드A", "재고없음", 20000L, "어두운", List.of("태그1", "태그2"), 20L, 0, 4.0, 4);
        createProduct("상품B", "브랜드B", "재고있음", 30000L, "밝은", List.of("태그3", "태그4"), 10L, 300, 2.0, 4);
        createProduct("상품C", "브랜드C", "재고있음", 40000L, "어두운", List.of("태그5", "태그6"), 30L, 300, 4.0, 4);
        createProduct("상품D", "브랜드D", "재고있음", 50000L, "밝은", List.of("태그7", "태그8"), 30L, 300, 4.0, 4);

        SearchRequestDto requestDto = SearchRequestDto.builder().keyword("밝은")
            .minPrice(BigDecimal.valueOf(30000L)).maxPrice(BigDecimal.valueOf(50000L)).sortType("review").build();

        List<Product> products = productRepository.findProductsBySearchCondition(requestDto);

        assertThat(products).hasSize(2);
        assertThat(products.get(0).getName()).isEqualTo("상품D");
    }
}