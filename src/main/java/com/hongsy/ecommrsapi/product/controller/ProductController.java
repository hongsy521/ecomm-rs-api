package com.hongsy.ecommrsapi.product.controller;

import com.hongsy.ecommrsapi.product.dto.ProductResponseDto;
import com.hongsy.ecommrsapi.product.dto.SimpleProductResponseDto;
import com.hongsy.ecommrsapi.product.service.ProductService;
import com.hongsy.ecommrsapi.util.common.CommonResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    ResponseEntity<CommonResponse<List<SimpleProductResponseDto>>> getProducts(){
        List<SimpleProductResponseDto> productResponseDtos = productService.getProducts();
        return ResponseEntity.ok(new CommonResponse<>("전체 상품 조회가 완료되었습니다.",200,productResponseDtos));
    }

    @GetMapping("/{productId}")
    ResponseEntity<CommonResponse<ProductResponseDto>> getProduct(@PathVariable(name = "productId")Long productId){
        ProductResponseDto productResponseDto = productService.getProduct(productId);
        return ResponseEntity.ok(new CommonResponse<>("단건 상품 조회가 완료되었습니다.",200,productResponseDto));
    }

}
