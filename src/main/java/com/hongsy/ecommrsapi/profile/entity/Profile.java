package com.hongsy.ecommrsapi.profile.entity;

import com.hongsy.ecommrsapi.user.entity.User;
import com.hongsy.ecommrsapi.util.ListStringToJsonConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profile")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_purchase_price")
    private BigDecimal totalPurchasePrice;

    @Column(name = "avg_purchase_price")
    private BigDecimal avgPurchasePrice;

    @Column(name = "last_purchase_date")
    private LocalDate lastPurchaseDate;

    @Convert(converter = ListStringToJsonConverter.class)
    @Column(name = "category", columnDefinition = "jsonb")
    private List<String> preferredCategory;

    @Column(name = "purchase_count")
    private Integer purchaseCount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public static Profile createProfile(User user){
        Profile profile = Profile.builder()
            .totalPurchasePrice(BigDecimal.ZERO)
            .avgPurchasePrice(BigDecimal.ZERO)
            .lastPurchaseDate(null)
            .preferredCategory(Collections.emptyList())
            .purchaseCount(0)
            .user(user)
            .build();

        return profile;
    }

}
