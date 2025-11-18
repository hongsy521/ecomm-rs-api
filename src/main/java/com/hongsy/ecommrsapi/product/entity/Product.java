package com.hongsy.ecommrsapi.product.entity;

import com.hongsy.ecommrsapi.util.ListStringToJsonConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "brand_name")
    private String brandName;

    @Column(columnDefinition = "TEXT")
    private String info;
    private BigDecimal price;
    private String image;

    @Column(name = "color_group")
    private String colorGroup;

    @Convert(converter = ListStringToJsonConverter.class)
    @Column(name = "tags", columnDefinition = "jsonb")
    private List<String> tags;

    @Column(name = "order_amount_for_30d")
    private Long orderAmountFor30d;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "avg_review_score")
    private Double avgReviewScore;
}
