package com.hongsy.ecommrsapi.product.entity;

import com.hongsy.ecommrsapi.product.dto.ProductRequestDto;
import com.hongsy.ecommrsapi.user.entity.User;
import com.hongsy.ecommrsapi.util.common.BaseTimeEntity;
import com.hongsy.ecommrsapi.util.exception.CustomException;
import com.hongsy.ecommrsapi.util.exception.ErrorCode;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Product extends BaseTimeEntity {

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

    @Type(JsonBinaryType.class)
    @Column(name = "tags", columnDefinition = "jsonb")
    private List<String> tags;

    @Column(name = "order_amount_for_30d")
    private Long orderAmountFor30d;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "avg_review_score")
    private Double avgReviewScore;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", insertable = false, updatable = false)
    private User seller;

    @Column(name = "like_count")
    private Integer likeCount;

    public static Product registerProduct(Long sellerId, ProductRequestDto requestDto){
        Product product = Product.builder()
            .name(requestDto.getName())
            .brandName(requestDto.getBrandName())
            .info(requestDto.getInfo())
            .price(requestDto.getPrice())
            .image(requestDto.getImage())
            .colorGroup(requestDto.getColorGroup())
            .tags(requestDto.getTags())
            .orderAmountFor30d(0L)
            .stockQuantity(requestDto.getStockQuantity())
            .avgReviewScore(0.0)
            .sellerId(sellerId)
            .build();

        return product;
    }

    public void editProduct(ProductRequestDto requestDto) {
        if(requestDto.getPrice().compareTo(BigDecimal.ZERO)<0 || requestDto.getStockQuantity().compareTo(0)<0){
            throw new CustomException(ErrorCode.INVALID_NUMBER);
        }
        this.name=requestDto.getName();
        this.brandName= requestDto.getBrandName();
        this.info= requestDto.getInfo();
        this.price=requestDto.getPrice();
        this.image= requestDto.getImage();
        this.colorGroup=requestDto.getColorGroup();
        this.tags=requestDto.getTags();
        this.stockQuantity= requestDto.getStockQuantity();
    }
}
