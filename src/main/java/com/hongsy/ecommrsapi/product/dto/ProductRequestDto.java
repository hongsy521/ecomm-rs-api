package com.hongsy.ecommrsapi.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductRequestDto {
    @NotBlank(message = "상품 이름은 필수 입력 사항 입니다.")
    private String name;
    @NotBlank(message = "브랜드 이름은 필수 입력 사항 입니다.")
    private String brandName;
    private String info;
    @NotNull(message = "가격은 필수 입력 사항 입니다.")
    private BigDecimal price;
    @NotBlank(message = "상품 이미지는 필수 첨부 사항 입니다.")
    private String image;
    private String colorGroup;
    private List<String> tags;
    @NotNull(message = "상품 재고 수량은 필수 입력 사항 입니다.")
    private Integer stockQuantity;

}
