package com.hongsy.ecommrsapi.product.repository;

import com.hongsy.ecommrsapi.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {

    Page<Product> findAllBySellerId(Long sellerId, Pageable pageable);
}
