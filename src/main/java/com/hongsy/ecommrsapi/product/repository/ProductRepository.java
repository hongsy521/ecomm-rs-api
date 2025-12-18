package com.hongsy.ecommrsapi.product.repository;

import com.hongsy.ecommrsapi.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductCustomRepository {

    Page<Product> findAllBySellerId(Long sellerId, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE product p set like_count=(
        select count(pl.id) from product_like pl where p.id=pl.product_id group by pl.product_id
        ) where p.id in (
        select distinct pl.product_id from product_like pl
        )""", nativeQuery = true)
    Integer bulkUpdateLikeCounts();

    @Modifying
    @Transactional
    @Query(value = """
                update product p set like_count=0 where p.like_count>0 and p.id not in (
                select distinct pl.product_id from product_like pl
                )
        """, nativeQuery = true)
    Integer bulkResetZeroLikeCounts();
}
