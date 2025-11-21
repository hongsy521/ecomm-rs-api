package com.hongsy.ecommrsapi.product.controller;

import com.hongsy.ecommrsapi.product.dto.ProductResponseDto;
import com.hongsy.ecommrsapi.product.dto.RegisterProductRequestDto;
import com.hongsy.ecommrsapi.product.service.SellerProductService;
import com.hongsy.ecommrsapi.util.UserDetailsImpl;
import com.hongsy.ecommrsapi.util.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/seller/product")
@RestController
@RequiredArgsConstructor
public class SellerProductController {

    private final SellerProductService sellerProductService;

    @PostMapping("/register")
    public ResponseEntity<CommonResponse<ProductResponseDto>> registerProduct(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody RegisterProductRequestDto requestDto){
        ProductResponseDto productResponseDto = sellerProductService.registerProduct(userDetails.getId(),requestDto);
        return ResponseEntity.ok(new CommonResponse("상품 등록이 완료되었습니다.",200,productResponseDto));
    }
}
