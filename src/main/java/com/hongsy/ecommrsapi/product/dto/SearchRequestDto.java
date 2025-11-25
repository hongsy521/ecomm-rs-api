package com.hongsy.ecommrsapi.product.dto;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class SearchRequestDto {
    private String keyword;
    private BigDecimal maxPrice;
    private BigDecimal minPrice;

}
