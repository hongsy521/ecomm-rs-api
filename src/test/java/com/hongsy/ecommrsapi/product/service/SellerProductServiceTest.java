package com.hongsy.ecommrsapi.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.hongsy.ecommrsapi.product.dto.ProductRequestDto;
import com.hongsy.ecommrsapi.product.dto.ProductResponseDto;
import com.hongsy.ecommrsapi.product.entity.ColorGroup;
import com.hongsy.ecommrsapi.product.entity.Product;
import com.hongsy.ecommrsapi.product.repository.ProductRepository;
import com.hongsy.ecommrsapi.util.exception.CustomException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("SellerProductService 단위 테스트")
class SellerProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private SellerProductService sellerProductService;

    @Test
    @DisplayName("상품 등록 테스트 : 필수 정보를 포함한 상품 정보 등록 성공 확인")
    void registerProduct_Success(){
        Long sellerId=24L;

        // given
        ProductRequestDto requestDto = ProductRequestDto.builder()
            .name("보트넥 긴팔티")
            .brandName("릿킴")
            .info("겨울 전용 기모 긴팔T 입니다.")
            .price(new BigDecimal(36000))
            .image("url")
            .colorGroup("블랙")
            .tags(List.of("기모", "긴팔T", "보트넥", "블랙"))
            .stockQuantity(300)
            .build();

        ColorGroup colorGroup = ColorGroup.colorGroupFromKorean(requestDto.getColorGroup());

        Product mockProduct = Product.registerProduct(sellerId,requestDto,colorGroup);

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

    @Test
    @DisplayName("상품 수정 성공 테스트 : 상품 정보 수정 확인")
    void testEditProductSuccess(){
        Long sellerId=24L;
        Long productId=25L;

        Product mockProduct = Product.builder()
            .id(productId)
            .name("보트넥 긴팔티")
            .brandName("릿킴")
            .info("겨울 전용 기모 긴팔T 입니다.")
            .price(new BigDecimal(36000))
            .image("url")
            .colorGroup(ColorGroup.BLACK)
            .tags(List.of("기모", "긴팔T", "보트넥", "블랙"))
            .orderAmountFor30d(0L)
            .stockQuantity(300)
            .avgReviewScore(0.0)
            .sellerId(sellerId)
            .likeCount(0)
            .build();

        // given
        ProductRequestDto editRequestDto = ProductRequestDto.builder()
            .name("패딩 점퍼")
            .brandName("릿킴")
            .info("한겨울에도 입을 수 있는 패딩 점퍼 입니다.")
            .price(new BigDecimal(123000))
            .image("url")
            .colorGroup("블랙")
            .tags(List.of("한겨울", "패딩", "점퍼", "카키색","따뜻함"))
            .stockQuantity(500)
            .build();

        given(productRepository.findById(productId)).willReturn(Optional.of(mockProduct));

        // when
        ProductResponseDto responseDto = sellerProductService.editProduct(sellerId,productId,editRequestDto);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getBrandName()).isEqualTo("릿킴");
        assertThat(responseDto.getStockQuantity()).isEqualTo(500);
        assertThat(responseDto.getTags())
            .isNotNull()
            .hasSize(5)
            .contains("따뜻함","한겨울");


        verify(productRepository,times(1)).findById(productId);
        verify(productRepository,times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("상품 수정 실패 테스트 : 상품 아이디가 존재하지 않는 경우 NOT_FOUND_PRODUCT 예외 발생 확인")
    void testEditNotFoundProduct(){
        Long sellerId=24L;
        Long nonExistingProductId=25L;

        ProductRequestDto requestDto = ProductRequestDto.builder().build();

        given(productRepository.findById(nonExistingProductId)).willReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,()->sellerProductService.editProduct(sellerId,nonExistingProductId,requestDto));

        assertThat(exception.getMessage()).isEqualTo("상품을 찾을 수 없습니다.");

        verify(productRepository,times(1)).findById(nonExistingProductId);
        verify(productRepository,times(0)).save(any(Product.class));
    }

    @Test
    @DisplayName("상품 수정 실패 테스트 : 상품의 소유자가 아닌 경우 INCORRECT_SELLER 예외 발생 확인")
    void testEditProductIncorrectSeller(){
        Long sellerId=24L;
        Long incorrectSellerId=23L;
        Long productId=25L;

        Product mockProduct = Product.builder()
            .id(productId)
            .sellerId(sellerId)
            .build();

        ProductRequestDto requestDto = ProductRequestDto.builder().build();

        given(productRepository.findById(productId)).willReturn(Optional.of(mockProduct));

        CustomException exception = assertThrows(CustomException.class,()-> sellerProductService.editProduct(incorrectSellerId,productId,requestDto));

        assertThat(exception.getMessage()).isEqualTo("판매자가 일치하지 않습니다.");

        verify(productRepository,times(1)).findById(productId);
        verify(productRepository,times(0)).save(any(Product.class));
    }

    @Test
    @DisplayName("상품 삭제 성공 테스트 : 상품 삭제 확인")
    void testDeleteProductSuccess(){
        Long sellerId=24L;
        Long productId=25L;

        Product mockProduct = Product.builder()
            .id(productId)
            .sellerId(sellerId)
            .build();

        given(productRepository.findById(productId)).willReturn(Optional.of(mockProduct));

        sellerProductService.deleteProduct(sellerId,productId);

        verify(productRepository,times(1)).findById(productId);
        verify(productRepository,times(1)).delete(any(Product.class));


    }

    @Test
    @DisplayName("상품 삭제 실패 테스트 : 상품 아이디가 존재하지 않는 경우 NOT_FOUND_PRODUCT 예외 발생 확인")
    void testDeleteNotFoundProduct(){
        Long sellerId=24L;
        Long nonExistingProductId=25L;

        given(productRepository.findById(nonExistingProductId)).willReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class,()->sellerProductService.deleteProduct(sellerId,nonExistingProductId));

        assertThat(exception.getMessage()).isEqualTo("상품을 찾을 수 없습니다.");

        verify(productRepository,times(1)).findById(nonExistingProductId);
        verify(productRepository,times(0)).delete(any(Product.class));
    }

    @Test
    @DisplayName("상품 삭제 실패 테스트 : 상품의 소유자가 아닌 경우 INCORRECT_SELLER 예외 발생 확인")
    void testDeleteProductIncorrectSeller(){
        Long sellerId=24L;
        Long incorrectSellerId=23L;
        Long productId=25L;

        Product mockProduct = Product.builder()
            .id(productId)
            .sellerId(sellerId)
            .build();

        given(productRepository.findById(productId)).willReturn(Optional.of(mockProduct));

        CustomException exception = assertThrows(CustomException.class,()->sellerProductService.deleteProduct(incorrectSellerId,productId));

        assertThat(exception.getMessage()).isEqualTo("판매자가 일치하지 않습니다.");

        verify(productRepository,times(1)).findById(productId);
        verify(productRepository,times(0)).delete(any(Product.class));
    }

    @Test
    @DisplayName("판매자 상품 조회 성공 테스트")
    void testGetProductsBySeller(){
        Long sellerId=24L;
        int page=0;
        int size=10;
        Pageable pageable = PageRequest.of(page,size);

        Product product1 = Product.builder().name("상품1").colorGroup(ColorGroup.BLACK).build();
        Product product2 = Product.builder().name("상품2").colorGroup(ColorGroup.BLACK).build();
        List<Product> productList = List.of(product1,product2);

        Page<Product> mockProductPage = new PageImpl<>(productList,pageable,productList.size());

        given(productRepository.findAllBySellerId(sellerId,pageable)).willReturn(mockProductPage);

        Page<ProductResponseDto> responseDtos = sellerProductService.getProductsBySeller(sellerId,page,size);

        assertThat(responseDtos).isNotNull();
        assertThat(responseDtos.getTotalElements()).isEqualTo(2);
        assertThat(responseDtos.getContent()).hasSize(2);
        assertThat(responseDtos.getContent().get(0).getName()).isEqualTo("상품1");
        assertThat(responseDtos.getContent().get(1).getColorGroup()).isEqualTo("블랙");

        verify(productRepository,times(1)).findAllBySellerId(sellerId,pageable);
    }

}