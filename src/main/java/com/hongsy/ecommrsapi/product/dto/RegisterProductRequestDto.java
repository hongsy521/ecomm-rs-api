package com.hongsy.ecommrsapi.product.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;

@Getter
public class RegisterProductRequestDto {
    private String name;
    private String brandName;
    private String info;
    private BigDecimal price;
    private String image;
    private String colorGroup;
    private List<String> tags;
    private Integer stockQuantity;

}
