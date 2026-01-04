package com.hongsy.ecommrsapi.product.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hongsy.ecommrsapi.product.entity.Product;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SimpleProductResponseDto {
    private Long id;
    private String name;
    private String brandName;
    private BigDecimal price;
    private String image;
    private List<String> tags;

    public SimpleProductResponseDto(Product product){
        this.id= product.getId();
        this.name=product.getName();
        this.brandName= product.getBrandName();
        this.price=product.getPrice();
        this.image= product.getImage();
        this.tags=product.getTags();
    }

    public SimpleProductResponseDto(Long id, String name, String brandName,
        BigDecimal price, String image, List<String> tags) {
        this.id = id;
        this.name = name;
        this.brandName = brandName;
        this.price = price;
        this.image = image;
        this.tags = tags;
    }

}
