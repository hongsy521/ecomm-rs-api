package com.hongsy.ecommrsapi.like.repository;

import com.hongsy.ecommrsapi.like.entity.ProductLike;
import com.hongsy.ecommrsapi.product.entity.Product;
import com.hongsy.ecommrsapi.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductLikeRepository extends JpaRepository<ProductLike,Long> {

    ProductLike findByUserAndProduct(User user, Product product);
}
