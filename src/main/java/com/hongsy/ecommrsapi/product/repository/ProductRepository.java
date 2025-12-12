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
public interface ProductRepository extends JpaRepository<Product,Long>,ProductCustomRepository {

    Page<Product> findAllBySellerId(Long sellerId, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE Product p set p.likeCount=(
            select count(pl.id) from ProductLike pl where p.id=pl.product.id group by pl.product.id
            ) where p.id in (
            select distinct pl.product.id from ProductLike pl
            )""",nativeQuery = true)
    Integer bulkUpdateLikeCounts();

    @Modifying
    @Transactional
    @Query(value = """
        update Product p set
        p.likeCount=0 where p.likeCount>0 and p.id not in (
        select distinct pl.product.id from ProductLike pl
        )
""",nativeQuery = true)
    Integer bulkResetZeroLikeCounts();
}
