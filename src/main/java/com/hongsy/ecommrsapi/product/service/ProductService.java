package com.hongsy.ecommrsapi.product.service;

import com.hongsy.ecommrsapi.product.dto.ProductResponseDto;
import com.hongsy.ecommrsapi.product.dto.SearchRequestDto;
import com.hongsy.ecommrsapi.product.dto.SimpleProductResponseDto;
import com.hongsy.ecommrsapi.product.entity.Product;
import com.hongsy.ecommrsapi.product.repository.ProductRepository;
import com.hongsy.ecommrsapi.util.LogExecutionTime;
import com.hongsy.ecommrsapi.util.exception.CustomException;
import com.hongsy.ecommrsapi.util.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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

    @LogExecutionTime
    public List<SimpleProductResponseDto> getProductsBySearchCondition(SearchRequestDto searchRequestDto){
        List<SimpleProductResponseDto> products = productRepository.findProductsBySearchCondition(searchRequestDto);

        return products;
    }

    @Transactional
    public void synchronizeLikeCounts(){
        long startTime = System.currentTimeMillis();

        int updatedCount = productRepository.bulkUpdateLikeCounts();
        int resetCount = productRepository.bulkResetZeroLikeCounts();

        long duration = System.currentTimeMillis() - startTime;

        System.out.printf("[Scheduler] 좋아요 동기화 완료 (업데이트: %d건, 초기화: %d건). 소요 시간: %dms\n",
            updatedCount, resetCount, duration);
    }

    public Product findById(Long productId){
        return productRepository.findById(productId).orElseThrow(
            ()-> new CustomException(ErrorCode.NOT_FOUND_PRODUCT)
        );
    }

}
