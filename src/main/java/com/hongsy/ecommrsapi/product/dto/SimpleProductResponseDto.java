package com.hongsy.ecommrsapi.product.dto;

import com.hongsy.ecommrsapi.product.entity.Product;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;

@Getter
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

}
