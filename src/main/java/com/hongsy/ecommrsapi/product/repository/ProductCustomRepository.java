package com.hongsy.ecommrsapi.product.repository;

import com.hongsy.ecommrsapi.product.entity.Product;
import java.util.List;

public interface ProductCustomRepository {

    List<Product> findProductsBySearchCondition();

    List<Product> findProductsByKeyword(String keyword);
}
