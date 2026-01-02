package com.hongsy.ecommrsapi.product.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchRequestDto {
    private String keyword;
    private BigDecimal maxPrice;
    private BigDecimal minPrice;
    private String sortType;

}
