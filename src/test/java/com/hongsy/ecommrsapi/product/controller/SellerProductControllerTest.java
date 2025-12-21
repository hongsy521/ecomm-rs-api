package com.hongsy.ecommrsapi.product.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.never;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hongsy.ecommrsapi.product.dto.ProductRequestDto;
import com.hongsy.ecommrsapi.product.dto.ProductResponseDto;
import com.hongsy.ecommrsapi.product.service.SellerProductService;
import com.hongsy.ecommrsapi.security.WithMockCustomUser;
import com.hongsy.ecommrsapi.util.config.SecurityConfig;
import com.hongsy.ecommrsapi.util.jwt.JwtTokenProvider;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = SellerProductController.class)
@Import({JwtTokenProvider.class, SecurityConfig.class})
@DisplayName("SellerProductController MockMvc 테스트")
class SellerProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    protected SellerProductService sellerProductService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockCustomUser(id = 1L, roles = {"판매자"})
    @DisplayName("T1-(1). 판매자 상품 등록 성공 테스트 - 등록 상품과 200 OK를 반환한다.")
    void registerProduct_ShouldSuccess() throws Exception {

        // given 1
        ProductResponseDto mockResponseDto = ProductResponseDto.builder()
            .id(1L)
            .name("보트넥 긴팔티")
            .brandName("릿킴")
            .info("겨울 전용 기모 긴팔T 입니다.")
            .price(new BigDecimal(36000))
            .image("url")
            .colorGroup("어두운")
            .tags(List.of("기모", "긴팔T", "보트넥", "블랙"))
            .stockQuantity(300)
            .orderAmountFor30d(0L)
            .avgReviewScore(0.0)
            .sellerId(1L)
            .build();

        // given 2
        ProductRequestDto requestDto = ProductRequestDto.builder()
            .name("보트넥 긴팔티")
            .brandName("릿킴")
            .info("겨울 전용 기모 긴팔T 입니다.")
            .price(new BigDecimal(36000))
            .image("url")
            .colorGroup("어두운")
            .tags(List.of("기모", "긴팔T", "보트넥", "블랙"))
            .stockQuantity(300)
            .build();

        // given 3
        given(sellerProductService.registerProduct(eq(1L),
            any(ProductRequestDto.class))).willReturn(
            mockResponseDto);

        // when
        mockMvc.perform(post("/api/seller/product/register").with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(requestDto))).andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("상품 등록이 완료되었습니다."))
            .andExpect(jsonPath("$.result.colorGroup").value("어두운"));

        // then
        verify(sellerProductService, times(1)).registerProduct(eq(1L),
            any(ProductRequestDto.class));
    }

    @Test
    @WithMockCustomUser(id = 1L, roles = {"판매자"})
    @DisplayName("T1-(2). 판매자 상품 등록 실패 테스트 - 상품명이 비어있는 경우 400 Bad Request를 반환하고 서비스를 호출하지 않는다.")
    void registerProduct_ShouldFail_WhenRequestIsInvalid() throws Exception {

        ProductRequestDto invalidRequestDto = ProductRequestDto.builder()
            .name("")
            .brandName("릿킴")
            .info("겨울 전용 기모 긴팔T 입니다.")
            .price(new BigDecimal(36000))
            .image("url")
            .colorGroup("어두운")
            .tags(List.of("기모", "긴팔T", "보트넥", "블랙"))
            .stockQuantity(300)
            .build();

        // when
        mockMvc.perform(post("/api/seller/product/register").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequestDto)))
            .andDo(print())
            .andExpect(jsonPath("$.message").value("상품 이름은 필수 입력 사항 입니다."))
            .andExpect(status().isBadRequest());

        // then
        verify(sellerProductService, never()).registerProduct(eq(1L), any(ProductRequestDto.class));

    }

    @Test
    @WithMockCustomUser(id = 1L, roles = {"구매자"})
    @DisplayName("T1-(3). 판매자 상품 등록 실패 테스트 - 판매자 권한을 갖지 않은 경우 403 Forbidden를 반환하고 서비스를 호출하지 않는다.")
    void registerProduct_ShouldFail_WhenRoleIsInvalid() throws Exception {

        mockMvc.perform(post("/api/seller/product/register").with(csrf())).andDo(print())
            .andExpect(status().isForbidden());

        verify(sellerProductService, never()).registerProduct(eq(1L), any(ProductRequestDto.class));
    }

    @Test
    @WithMockCustomUser(id = 1L, roles = {"판매자"})
    @DisplayName("T2-(1). 판매자 상품 수정 성공 테스트 - 수정 상품과 200 OK를 반환한다.")
    void editProduct_ShouldSuccess() throws Exception {
        Long productId = 25L;

        ProductResponseDto responseDto = ProductResponseDto.builder()
            .id(productId)
            .name("보트넥 긴팔티")
            .brandName("릿킴")
            .info("겨울 전용 기모 긴팔T 입니다.")
            .price(new BigDecimal(36000))
            .image("url")
            .colorGroup("밝은")
            .tags(List.of("기모", "긴팔T", "보트넥", "화이트"))
            .stockQuantity(300)
            .orderAmountFor30d(0L)
            .avgReviewScore(0.0)
            .sellerId(1L)
            .build();

        ProductRequestDto editRequestDto = ProductRequestDto.builder()
            .name("보트넥 긴팔티")
            .brandName("릿킴")
            .info("겨울 전용 기모 긴팔T 입니다.")
            .price(new BigDecimal(36000))
            .image("url")
            .colorGroup("밝은")
            .tags(List.of("기모", "긴팔T", "보트넥", "화이트"))
            .stockQuantity(300)
            .build();

        given(sellerProductService.editProduct(eq(1L), eq(productId),
            any(ProductRequestDto.class))).willReturn(
            responseDto);

        mockMvc.perform(
                put("/api/seller/product/edit/{productId}", productId).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(editRequestDto))).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("상품 편집이 완료되었습니다."))
            .andExpect(jsonPath("$.result.colorGroup").value("밝은"));

        verify(sellerProductService, times(1)).editProduct(eq(1L), eq(productId),
            any(ProductRequestDto.class));
    }

    @Test
    @WithMockCustomUser(id = 1L, roles = {"판매자"})
    @DisplayName("T2-(2). 판매자 상품 수정 실패 테스트 - 상품명이 비어있는 경우 400 Bad Request를 반환하고 서비스를 호출하지 않는다.")
    void editProduct_ShouldFail_WhenRequestIsInvalid() throws Exception {
        Long productId = 25L;

        ProductRequestDto invalidRequestDto = ProductRequestDto.builder()
            .name("")
            .brandName("릿킴")
            .info("겨울 전용 기모 긴팔T 입니다.")
            .price(new BigDecimal(36000))
            .image("url")
            .colorGroup("어두운")
            .tags(List.of("기모", "긴팔T", "보트넥", "블랙"))
            .stockQuantity(300)
            .build();

        // when
        mockMvc.perform(put("/api/seller/product/edit/{productId}", productId).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequestDto)))
            .andDo(print())
            .andExpect(jsonPath("$.message").value("상품 이름은 필수 입력 사항 입니다."))
            .andExpect(status().isBadRequest());

        // then
        verify(sellerProductService, never()).editProduct(eq(1L), eq(productId),
            any(ProductRequestDto.class));
    }

    @Test
    @WithMockCustomUser(id = 1L, roles = {"구매자"})
    @DisplayName("T2-(3). 판매자 상품 수정 실패 테스트 - 판매자 권한을 갖지 않은 경우 403 Forbidden를 반환하고 서비스를 호출하지 않는다.")
    void editProduct_ShouldFail_WhenRoleIsInvalid() throws Exception {
        Long productId = 25L;

        mockMvc.perform(put("/api/seller/product/edit/{productId}", productId).with(csrf()))
            .andDo(print())
            .andExpect(status().isForbidden());

        verify(sellerProductService, never()).editProduct(eq(1L), eq(productId),
            any(ProductRequestDto.class));
    }

    @Test
    @WithMockCustomUser(id = 1L, roles = {"판매자"})
    @DisplayName("T2-(4). 판매자 상품 수정 실패 테스트 - 잘못된 productId를 넘긴 경우 400 Bad Request를 반환하고 서비스를 호출하지 않는다.")
    void editProduct_ShouldFail_WhenIdIsNegative() throws Exception {
        Long invalidProductId = -999L;

        mockMvc.perform(
                put("/api/seller/product/edit/{invalidProductId}", invalidProductId).with(csrf()))
            .andDo(print())
            .andExpect(status().isBadRequest());

        verify(sellerProductService, never()).editProduct(eq(1L), eq(invalidProductId),
            any(ProductRequestDto.class));
    }

    @Test
    @WithMockCustomUser(id = 1L, roles = {"판매자"})
    @DisplayName("T3-(1). 판매자 상품 삭제 성공 테스트 - 상품 삭제를 완료하고 200 OK를 반환한다.")
    void deleteProduct_ShouldSuccess() throws Exception {
        Long productId = 25L;
        mockMvc.perform(delete("/api/seller/product/delete/{productId}", productId).with(csrf()))
            .andDo(print()).andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("상품 삭제가 완료되었습니다."));

        verify(sellerProductService, times(1)).deleteProduct(eq(1L), eq(productId));
    }

    @Test
    @WithMockCustomUser(id = 1L, roles = {"판매자"})
    @DisplayName("T3-(2). 판매자 상품 삭제 실패 테스트 - 잘못된 productId를 넘긴 경우 400 Bad Request를 반환하고 서비스를 호출하지 않는다.")
    void deleteProduct_ShouldFail_WhenIdIsNegative() throws Exception {
        Long invalidProductId = -999L;

        mockMvc.perform(
                delete("/api/seller/product/delete/{invalidProductId}", invalidProductId).with(csrf()))
            .andDo(print())
            .andExpect(status().isBadRequest());

        verify(sellerProductService, never()).deleteProduct(eq(1L), eq(invalidProductId));
    }

    @Test
    @WithMockCustomUser(id = 1L, roles = {"구매자"})
    @DisplayName("T3-(3). 판매자 상품 삭제 실패 테스트 - 판매자 권한을 갖지 않은 경우 403 Forbidden를 반환하고 서비스를 호출하지 않는다.")
    void deleteProduct_ShouldFail_WhenRoleIsInvalid() throws Exception {
        Long productId = 25L;

        mockMvc.perform(delete("/api/seller/product/delete/{productId}", productId).with(csrf()))
            .andDo(print())
            .andExpect(status().isForbidden());

        verify(sellerProductService, never()).deleteProduct(eq(1L), eq(productId));
    }

    @Test
    @WithMockCustomUser(id = 1L, roles = {"판매자"})
    @DisplayName("T4-(1). 판매자 상품 전체 조회 성공 테스트 - 200 OK와 상품 목록을 반환한다.")
    void getAllProductOfSeller_ShouldSuccess() throws Exception {
        int requestPage = 1;
        int requestSize = 5;
        int expectedServicePage = 0;

        ProductResponseDto responseDto1 = ProductResponseDto.builder()
            .id(23L)
            .name("보트넥 긴팔티")
            .price(new BigDecimal("36000"))
            .sellerId(1L)
            .build();

        ProductResponseDto responseDto2 = ProductResponseDto.builder()
            .id(24L)
            .name("패딩 점퍼")
            .price(new BigDecimal("36000"))
            .sellerId(1L)
            .build();

        List<ProductResponseDto> responseDtoList = List.of(responseDto1, responseDto2);
        Page<ProductResponseDto> responseDtoPage = new PageImpl<>(responseDtoList, PageRequest.of(expectedServicePage, requestSize), responseDtoList.size());

        given(sellerProductService.getProductsBySeller(eq(1L), eq(expectedServicePage), eq(requestSize)))
            .willReturn(responseDtoPage);

        mockMvc.perform(get("/api/seller/product/all")
                .with(csrf())
                .param("page", String.valueOf(requestPage))
                .param("size", String.valueOf(requestSize)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("판매자의 모든 상품 조회가 완료되었습니다."))
            .andExpect(jsonPath("$.result.content[0].name").value("보트넥 긴팔티"))
            .andExpect(jsonPath("$.result.content.length()").value(2));

        verify(sellerProductService, times(1)).getProductsBySeller(eq(1L), eq(expectedServicePage), eq(requestSize));
    }

    @Test
    @WithMockCustomUser(id = 1L, roles = {"구매자"})
    @DisplayName("T4-(2). 판매자 상품 전체 조회 실패 테스트 - 판매자 권한을 갖지 않은 경우 403 Forbidden를 반환하고 서비스를 호출하지 않는다.")
    void getAllProductOfSeller_ShouldFail_WhenRoleIsInvalid() throws Exception {
        mockMvc.perform(get("/api/seller/product/all").with(csrf()))
            .andDo(print())
            .andExpect(status().isForbidden());

        verify(sellerProductService, never()).getProductsBySeller(eq(1L), any(int.class),any(int.class));
    }

    @Test
    @WithMockCustomUser(id = 1L, roles = {"판매자"})
    @DisplayName("T4-(3). 판매자 상품 전체 조회 실패 테스트 - 유효하지 않은 page, size을 param으로 넘긴 경우 400 Bad Request를 반환하고 서비스를 호출하지 않는다.")
    void getAllProductOfSeller_ShouldFail_WhenParamIsInvalid() throws Exception {
        int requestPage = 0;
        int requestSize = -5;
        int expectedServicePage = -1;

        mockMvc.perform(get("/api/seller/product/all")
                .with(csrf())
                .param("page", String.valueOf(requestPage))
                .param("size", String.valueOf(requestSize)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("must be greater than or equal to 1"));

        verify(sellerProductService, never()).getProductsBySeller(eq(1L), eq(expectedServicePage), eq(requestSize));
    }


}