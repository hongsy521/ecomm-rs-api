package com.hongsy.ecommrsapi.product.service;

import com.hongsy.ecommrsapi.product.dto.ProductResponseDto;
import com.hongsy.ecommrsapi.product.dto.SearchRequestDto;
import com.hongsy.ecommrsapi.product.dto.SimpleProductResponseDto;
import com.hongsy.ecommrsapi.product.entity.Product;
import com.hongsy.ecommrsapi.product.repository.ProductRepository;
import com.hongsy.ecommrsapi.util.exception.CustomException;
import com.hongsy.ecommrsapi.util.exception.ErrorCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public ProductResponseDto getProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
            () -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT)
        );

        return new ProductResponseDto(product);
    }

    public List<SimpleProductResponseDto> getProductsBySearchCondition(SearchRequestDto searchRequestDto){
        List<Product> products = productRepository.findProductsBySearchCondition(searchRequestDto);

        return products.stream().map(SimpleProductResponseDto::new).collect(Collectors.toList());
    }

    @Transactional
    public void synchronizeLikeCounts(){
        productRepository.bulkUpdateLikeCounts();
        productRepository.bulkResetZeroLikeCounts();
    }

    public Product findById(Long productId){
        return productRepository.findById(productId).orElseThrow(
            ()-> new CustomException(ErrorCode.NOT_FOUND_PRODUCT)
        );
    }

}
