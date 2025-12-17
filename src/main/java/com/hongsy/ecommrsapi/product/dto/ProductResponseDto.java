package com.hongsy.ecommrsapi.product.dto;

import com.hongsy.ecommrsapi.product.entity.Product;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProductResponseDto {
    private Long id;
    private String name;
    private String brandName;
    private String info;
    private BigDecimal price;
    private String image;
    private String colorGroup;
    private List<String> tags;
    private Integer stockQuantity;
    private Long orderAmountFor30d;
    private Double avgReviewScore;
    private Long sellerId;

    public ProductResponseDto(Product product) {
        this.id= product.getId();
        this.name=product.getName();
        this.brandName= product.getBrandName();
        this.info= product.getInfo();
        this.price=product.getPrice();
        this.image= product.getImage();
        this.colorGroup=product.getColorGroup();
        this.tags=product.getTags();
        this.stockQuantity= product.getStockQuantity();
        this.orderAmountFor30d= product.getOrderAmountFor30d();
        this.avgReviewScore=product.getAvgReviewScore();
        this.sellerId= product.getSellerId();
    }
}
