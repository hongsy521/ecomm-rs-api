package com.hongsy.ecommrsapi.product.service;

import com.hongsy.ecommrsapi.product.dto.ProductResponseDto;
import com.hongsy.ecommrsapi.product.dto.SimpleProductResponseDto;
import com.hongsy.ecommrsapi.product.entity.Product;
import com.hongsy.ecommrsapi.product.repository.ProductRepository;
import com.hongsy.ecommrsapi.util.exception.CustomException;
import com.hongsy.ecommrsapi.util.exception.ErrorCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<SimpleProductResponseDto> getProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(SimpleProductResponseDto::new).collect(Collectors.toList());
    }

    public ProductResponseDto getProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
            () -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT)
        );

        return new ProductResponseDto(product);
    }

    public List<SimpleProductResponseDto> getProductsByKeyword(String keyword) {
        List<Product> products = productRepository.findProductsByKeyword(keyword);

        return products.stream().map(SimpleProductResponseDto::new).collect(Collectors.toList());
    }
}
