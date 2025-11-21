package com.hongsy.ecommrsapi.product.service;

import com.hongsy.ecommrsapi.product.dto.ProductResponseDto;
import com.hongsy.ecommrsapi.product.dto.RegisterProductRequestDto;
import com.hongsy.ecommrsapi.product.entity.Product;
import com.hongsy.ecommrsapi.product.repository.ProductRepository;
import com.hongsy.ecommrsapi.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponseDto registerProduct(Long sellerId, RegisterProductRequestDto requestDto) {
        Product product = Product.registerProduct(sellerId,requestDto);
        productRepository.save(product);

        return new ProductResponseDto(product);
    }
}
