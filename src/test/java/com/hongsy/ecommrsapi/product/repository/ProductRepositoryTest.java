package com.hongsy.ecommrsapi.product.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.hongsy.ecommrsapi.product.entity.Product;
import com.hongsy.ecommrsapi.user.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;


@Transactional
class ProductRepositoryTest extends AbstractIntegrationTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("판매자 ID로 페이징된 상품 목록을 조회한다.")
    void testFindAllBySellerId(){
        User sellerA = User.builder()
            .name("판매자A")
            .email("sellerA@test.com")
            .password("sellerA1234")
            .build();
        em.persist(sellerA);

        User sellerB = User.builder()
            .name("판매자B")
            .email("sellerB@test.com")
            .password("sellerB1234")
            .build();
        em.persist(sellerB);

        Product product1 = Product.builder()
            .name("상품1")
            .sellerId(sellerA.getId())
            .build();

        Product product2 = Product.builder()
            .name("상품2")
            .sellerId(sellerA.getId())
            .build();

        Product product3 = Product.builder()
            .name("상품3")
            .sellerId(sellerB.getId())
            .build();

        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        em.flush();
        em.clear();

        PageRequest pageRequest = PageRequest.of(0,10);

        Page<Product> result = productRepository.findAllBySellerId(sellerA.getId(),pageRequest);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).extracting(Product::getName).containsExactlyInAnyOrder("상품1","상품2");
        assertThat(result.getContent()).allMatch(product -> product.getSellerId().equals(sellerA.getId()));
    }
}