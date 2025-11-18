package com.hongsy.ecommrsapi.user.entity;

import com.hongsy.ecommrsapi.util.ListStringToJsonConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_purchase_price")
    private BigDecimal totalPurchasePrice;

    @Column(name = "avg_purchase_price")
    private BigDecimal avgPurchasePrice;

    @Column(name = "last_purchase_date")
    private LocalDateTime lastPurchaseDate;

    @Convert(converter = ListStringToJsonConverter.class)
    @Column(name = "category", columnDefinition = "jsonb")
    private List<String> preferredCategory;

    @Column(name = "purchase_count")
    private Integer purchaseCount;

}
