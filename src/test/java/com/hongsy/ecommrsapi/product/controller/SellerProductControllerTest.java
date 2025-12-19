package com.hongsy.ecommrsapi.product.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.never;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = SellerProductController.class)
@Import({JwtTokenProvider.class,SecurityConfig.class})
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
    void testRegisterProductSuccess() throws Exception {

        // given 1
        ProductResponseDto mockResponseDto = ProductResponseDto.builder()
            .id(1L)
            .name("보트넥 긴팔티")
            .brandName("릿킴")
            .info("겨울 전용 기모 긴팔T 입니다.")
            .price(new BigDecimal(36000))
            .image("url")
            .colorGroup("검정색")
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
            .colorGroup("검정색")
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
            .andExpect(jsonPath("$.result.colorGroup").value("검정색"));

        // then
        verify(sellerProductService, times(1)).registerProduct(eq(1L),
            any(ProductRequestDto.class));
    }

    @Test
    @WithMockCustomUser(id = 1L, roles = {"판매자"})
    @DisplayName("T1-(2). 판매자 상품 등록 실패 테스트 - 상품명이 비어있는 경우 400 Bad Request를 반환하고 서비스를 호출하지 않는다.")
    void testRegisterProductFailed400() throws Exception {

        ProductRequestDto invalidRequestDto = ProductRequestDto.builder()
            .name("")
            .brandName("릿킴")
            .info("겨울 전용 기모 긴팔T 입니다.")
            .price(new BigDecimal(36000))
            .image("url")
            .colorGroup("검정색")
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
    void testRegisterProductFailed403() throws Exception {

        mockMvc.perform(post("/api/seller/product/register").with(csrf())).andDo(print())
            .andExpect(status().isForbidden());

        verify(sellerProductService, never()).registerProduct(eq(1L), any(ProductRequestDto.class));
    }

    @Test
    @DisplayName("T2-(1). 판매자 상품 수정 성공 테스트 - 수정 상품과 200 OK를 반환한다.")
    void testEditProductSuccess() throws Exception{
        ProductResponseDto responseDto = ProductResponseDto.builder()
            .id(1L)
            .name("보트넥 긴팔티")
            .brandName("릿킴")
            .info("겨울 전용 기모 긴팔T 입니다.")
            .price(new BigDecimal(36000))
            .image("url")
            .colorGroup("검정색")
            .tags(List.of("기모", "긴팔T", "보트넥", "블랙"))
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
            .colorGroup("검정색")
            .tags(List.of("기모", "긴팔T", "보트넥", "블랙"))
            .stockQuantity(300)
            .build();
    }


}