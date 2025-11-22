package com.hongsy.ecommrsapi.product.repository;

import com.hongsy.ecommrsapi.product.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {

    List<Product> findAllBySellerId(Long sellerId);
}
