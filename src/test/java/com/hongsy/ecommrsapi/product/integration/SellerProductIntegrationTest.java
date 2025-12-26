package com.hongsy.ecommrsapi.product.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hongsy.ecommrsapi.product.dto.ProductRequestDto;
import com.hongsy.ecommrsapi.product.entity.Product;
import com.hongsy.ecommrsapi.product.repository.ProductRepository;
import com.hongsy.ecommrsapi.user.dto.LoginRequestDto;
import com.hongsy.ecommrsapi.user.dto.SignupRequestDto;
import com.hongsy.ecommrsapi.user.repository.UserRepository;
import com.hongsy.ecommrsapi.util.FullIntegrationTest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class SellerProductIntegrationTest extends FullIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("회원가입 후 로그인하여 판매자가 상품을 등록하는 통합 시나리오")
    void FullRegisterProductScenarioTest() throws Exception {
        SignupRequestDto signupRequestDto = SignupRequestDto.builder().name("tester")
            .email("test@test.com")
            .gender("여성")
            .password("password123").roles(Set.of("판매자")).build();

        mockMvc.perform(
                post("/api/user/signup").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(signupRequestDto))).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."));

        assertThat(userRepository.findByEmail("test@test.com")).isPresent();

        LoginRequestDto loginRequestDto = LoginRequestDto.builder()
            .email("test@test.com").password("password123").build();

        MvcResult loginResult = mockMvc.perform(
                post("/api/user/login").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(loginRequestDto))).andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("로그인이 완료되었습니다."))
            .andExpect(cookie().exists("refreshToken"))
            .andExpect(cookie().httpOnly("refreshToken", true)).andReturn();

        String loginResponseContent = loginResult.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(loginResponseContent);
        String accessToken = root.path("result").asText();

        ProductRequestDto productRequestDto = ProductRequestDto.builder()
            .name("테스트 상품")
            .brandName("브랜드 이름")
            .image("url")
            .stockQuantity(300)
            .price(BigDecimal.valueOf(10000))
            .build();

        mockMvc.perform(
                post("/api/seller/product/register").with(csrf()).header("Authorization", accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productRequestDto))).andDo(print())
            .andExpect(status().isOk()).andExpect(jsonPath("$.message").value("상품 등록이 완료되었습니다."));

        List<Product> products = productRepository.findAll();
        assertThat(products).anyMatch(p -> p.getName().equals("테스트 상품"));
    }
}
