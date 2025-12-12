package com.hongsy.ecommrsapi.like.service;

import com.hongsy.ecommrsapi.like.entity.ProductLike;
import com.hongsy.ecommrsapi.like.repository.ProductLikeRepository;
import com.hongsy.ecommrsapi.product.entity.Product;
import com.hongsy.ecommrsapi.product.service.ProductService;
import com.hongsy.ecommrsapi.user.entity.User;
import com.hongsy.ecommrsapi.util.exception.CustomException;
import com.hongsy.ecommrsapi.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductLikeService {
    private final ProductLikeRepository productLikeRepository;
    private final ProductService productService;

    @Transactional
    public void createLike(User user, Long productId) {
        Product product = productService.findById(productId);
        ProductLike productLike = productLikeRepository.findByUserAndProduct(user,product);

        if(productLike !=null){
            throw new CustomException(ErrorCode.EXISTING_LIKE);
        }
        ProductLike newProductLike = ProductLike.createLike(user,product);
        productLikeRepository.save(newProductLike);
    }

    @Transactional
    public void deleteLike(User user, Long productId) {
        Product product = productService.findById(productId);
        ProductLike productLike = productLikeRepository.findByUserAndProduct(user,product);

        if(productLike ==null){
            throw new CustomException(ErrorCode.NOT_FOUND_LIKE);
        }
        productLikeRepository.delete(productLike);
    }
}
