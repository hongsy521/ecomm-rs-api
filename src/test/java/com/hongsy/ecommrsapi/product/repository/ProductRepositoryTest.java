package com.hongsy.ecommrsapi.product.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.hongsy.ecommrsapi.product.entity.Product;
import com.hongsy.ecommrsapi.user.entity.User;
import com.hongsy.ecommrsapi.util.FullIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@DisplayName("ProductRepository 슬라이스 테스트")
class ProductRepositoryTest extends FullIntegrationTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("판매자 ID로 페이징된 상품 목록 조회 테스트")
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

    @Test
    @DisplayName("벌크 연산 테스트 : 상품 좋아요 갯수 업데이트 성공 확인")
    void testBulkUpdateLikeCounts(){
        User seller = User.builder()
            .name("판매자")
            .email("seller@test.com")
            .password("seller1234")
            .build();
        em.persist(seller);

        User likeUser1 = User.builder()
            .name("사용자1")
            .email("user1@test.com")
            .password("user1111")
            .build();

        User likeUser2 = User.builder()
            .name("사용자2")
            .email("user2@test.com")
            .password("user2222")
            .build();
        em.persist(likeUser1);
        em.persist(likeUser2);

        Product product1 = Product.builder()
            .sellerId(seller.getId())
            .likeCount(0)
            .build();
        Product product2 = Product.builder()
            .sellerId(seller.getId())
            .likeCount(0)
            .build();
        Product product3 = Product.builder()
            .sellerId(seller.getId())
            .likeCount(0)
            .build();
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        em.flush();
        em.clear();

        insertLike(product1,likeUser1);
        insertLike(product1,likeUser2);
        insertLike(product2,likeUser1);

        em.flush();
        em.clear();

        Integer result = productRepository.bulkUpdateLikeCounts();

        assertThat(result).isEqualTo(2);

        Product updateProduct1 = productRepository.findById(product1.getId()).orElseThrow();
        Product updateProduct2 = productRepository.findById(product2.getId()).orElseThrow();
        Product updateProduct3 = productRepository.findById(product3.getId()).orElseThrow();

        assertThat(updateProduct1.getLikeCount()).isEqualTo(2);
        assertThat(updateProduct2.getLikeCount()).isEqualTo(1);
        assertThat(updateProduct3.getLikeCount()).isEqualTo(0);
    }

    private void insertLike(Product product, User user){
        em.createNativeQuery("INSERT INTO product_like (product_id, user_id) values (?,?)")
            .setParameter(1,product.getId())
            .setParameter(2,user.getId())
            .executeUpdate();
    }

    private void deleteLike(Product product){
        em.createNativeQuery("DELETE FROM product_like WHERE (product_id=?)")
            .setParameter(1,product.getId())
            .executeUpdate();
    }

    @Test
    @DisplayName("벌크 연산 테스트 : 상품 좋아요 갯수 초기화 성공 확인")
    void testBulkResetZeroLikeCounts(){
        User seller = User.builder()
            .name("판매자")
            .email("seller@test.com")
            .password("seller1234")
            .build();
        em.persist(seller);

        Product product = Product.builder()
            .likeCount(1)
            .sellerId(seller.getId())
            .build();
        productRepository.save(product);

        em.flush();
        em.clear();

        Integer result = productRepository.bulkResetZeroLikeCounts();

        em.clear();

        assertThat(result).isEqualTo(1);

        Product updateProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updateProduct.getLikeCount()).isEqualTo(0);
    }
}