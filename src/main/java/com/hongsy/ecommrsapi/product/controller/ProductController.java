package com.hongsy.ecommrsapi.product.controller;

import com.hongsy.ecommrsapi.product.dto.ProductResponseDto;
import com.hongsy.ecommrsapi.product.dto.SimpleProductResponseDto;
import com.hongsy.ecommrsapi.product.service.ProductService;
import com.hongsy.ecommrsapi.util.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Product API",description = "상품 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "일반 사용자 - 단건 상품 조회")
    @GetMapping("/{productId}")
    ResponseEntity<CommonResponse<ProductResponseDto>> getProduct(@PathVariable(name = "productId")Long productId){
        ProductResponseDto productResponseDto = productService.getProduct(productId);
        return ResponseEntity.ok(new CommonResponse<>("단건 상품 조회가 완료되었습니다.",200,productResponseDto));
    }

    @Operation(summary = "일반 사용자 - 상품 전체 조회 및 키워드 검색 조회")
    @GetMapping
    ResponseEntity<CommonResponse<List<SimpleProductResponseDto>>> getProductsByKeyword(@RequestParam(required = false) String keyword){
        List<SimpleProductResponseDto> productResponseDtos = productService.getProductsByKeyword(keyword);
        return ResponseEntity.ok(new CommonResponse<>("상품 조회가 완료되었습니다.",200,productResponseDtos));
    }

}
