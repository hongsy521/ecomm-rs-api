package com.hongsy.ecommrsapi.product.controller;

import com.hongsy.ecommrsapi.product.dto.ProductResponseDto;
import com.hongsy.ecommrsapi.product.dto.ProductRequestDto;
import com.hongsy.ecommrsapi.product.service.SellerProductService;
import com.hongsy.ecommrsapi.util.UserDetailsImpl;
import com.hongsy.ecommrsapi.util.common.CommonResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/seller/product")
@RestController
@RequiredArgsConstructor
public class SellerProductController {

    private final SellerProductService sellerProductService;

    @PostMapping("/register")
    public ResponseEntity<CommonResponse<ProductResponseDto>> registerProduct(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody ProductRequestDto requestDto){
        ProductResponseDto productResponseDto = sellerProductService.registerProduct(userDetails.getId(),requestDto);
        return ResponseEntity.ok(new CommonResponse("상품 등록이 완료되었습니다.",200,productResponseDto));
    }

    @PutMapping("/edit/{productId}")
    public ResponseEntity<CommonResponse<ProductResponseDto>> editProduct(@AuthenticationPrincipal UserDetailsImpl userDetails,@PathVariable(name = "productId")Long productId,@RequestBody ProductRequestDto requestDto){
        ProductResponseDto productResponseDto = sellerProductService.editProduct(userDetails.getId(),productId,requestDto);
        return ResponseEntity.ok(new CommonResponse<>("상품 편집이 완료되었습니다.",200,productResponseDto));
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<CommonResponse> deleteProduct(@AuthenticationPrincipal UserDetailsImpl userDetails,@PathVariable(name = "productId")Long productId){
        sellerProductService.deleteProduct(userDetails.getId(),productId);
        return ResponseEntity.ok(new CommonResponse("상품 삭제가 완료되었습니다.",200,""));
    }

    @GetMapping("/all")
    public ResponseEntity<CommonResponse<Page<ProductResponseDto>>> getAllProductOfSeller(@AuthenticationPrincipal UserDetailsImpl userDetails,@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "8")int size){
        Page<ProductResponseDto> productResponseDtos = sellerProductService.getProductsBySeller(userDetails.getId(),page-1,size);
        return ResponseEntity.ok(new CommonResponse<>("판매자의 모든 상품 조회가 완료되었습니다.",200,productResponseDtos));
    }
}
