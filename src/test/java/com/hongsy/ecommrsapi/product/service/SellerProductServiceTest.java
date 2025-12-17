package com.hongsy.ecommrsapi.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.hongsy.ecommrsapi.product.dto.ProductRequestDto;
import com.hongsy.ecommrsapi.product.dto.ProductResponseDto;
import com.hongsy.ecommrsapi.product.entity.Product;
import com.hongsy.ecommrsapi.product.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SellerProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private SellerProductService sellerProductService;

    @Test
    @DisplayName("상품 등록 테스트 : 필수 정보를 포함한 상품 정보 등록 성공 확인")
    void testRegisterProduct(){
        Long sellerId=24L;

        // given
        ProductRequestDto requestDto = ProductRequestDto.builder()
            .name("보트넥 긴팔티")
            .brandName("릿킴")
            .info("겨울 전용 기모 긴팔T 입니다.")
            .price(new BigDecimal(36000))
            .image("url")
            .colorGroup("검정색")
            .tags(List.of("기모", "긴팔T", "보트넥", "블랙"))
            .stockQuantity(300)
            .build();

        Product mockProduct = Product.registerProduct(sellerId,requestDto);

        // stubbing
        given(productRepository.save(any(Product.class))).willReturn(mockProduct);

        // when
        ProductResponseDto responseDto = sellerProductService.registerProduct(sellerId,requestDto);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getBrandName()).isEqualTo("릿킴");
        assertThat(responseDto.getStockQuantity()).isEqualTo(300);
        assertThat(responseDto.getTags())
            .isNotNull()
            .hasSize(4)
            .contains("기모","블랙");

        verify(productRepository,times(1)).save(any(Product.class));
    }

}