package com.hongsy.ecommrsapi.product.service;

import com.hongsy.ecommrsapi.product.dto.ProductResponseDto;
import com.hongsy.ecommrsapi.product.dto.ProductRequestDto;
import com.hongsy.ecommrsapi.product.entity.Product;
import com.hongsy.ecommrsapi.product.repository.ProductRepository;
import com.hongsy.ecommrsapi.util.UserDetailsImpl;
import com.hongsy.ecommrsapi.util.exception.CustomException;
import com.hongsy.ecommrsapi.util.exception.ErrorCode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponseDto registerProduct(Long sellerId, ProductRequestDto requestDto) {
        Product product = Product.registerProduct(sellerId,requestDto);
        productRepository.save(product);

        return new ProductResponseDto(product);
    }

    @Transactional
    public ProductResponseDto editProduct(Long sellerId, Long productId, ProductRequestDto requestDto) {
        Product product = productRepository.findById(productId).orElseThrow(
            ()-> new CustomException(ErrorCode.NOT_FOUND_PRODUCT)
        );
        if(product.getSellerId()!=sellerId){
            throw new CustomException(ErrorCode.INCORRECT_SELLER);
        }
        product.editProduct(requestDto);
        productRepository.save(product);

        return new ProductResponseDto(product);
    }

    @Transactional
    public void deleteProduct(Long sellerId, Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
            ()-> new CustomException(ErrorCode.NOT_FOUND_PRODUCT)
        );
        if(product.getSellerId()!=sellerId){
            throw new CustomException(ErrorCode.INCORRECT_SELLER);
        }
        productRepository.delete(product);
    }

    public Page<ProductResponseDto> getProductsBySeller(Long sellerId, int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        Page<Product> products = productRepository.findAllBySellerId(sellerId,pageable);

        return products.map(ProductResponseDto::new);

    }
}
