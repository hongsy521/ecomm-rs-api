package com.hongsy.ecommrsapi.product.controller;

import com.hongsy.ecommrsapi.product.dto.ProductRequestDto;
import com.hongsy.ecommrsapi.product.dto.ProductResponseDto;
import com.hongsy.ecommrsapi.product.service.SellerProductService;
import com.hongsy.ecommrsapi.util.UserDetailsImpl;
import com.hongsy.ecommrsapi.util.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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

@Tag(name = "Seller Product API", description = "판매자 상품 API")
@RequestMapping("/api/seller/product")
@RestController
@RequiredArgsConstructor
public class SellerProductController {

    private final SellerProductService sellerProductService;

    @Operation(summary = "판매자 - 상품 등록")
    @PostMapping("/register")
    public ResponseEntity<CommonResponse<ProductResponseDto>> registerProduct(@AuthenticationPrincipal UserDetailsImpl userDetails, @Valid @RequestBody ProductRequestDto requestDto){
        ProductResponseDto productResponseDto = sellerProductService.registerProduct(userDetails.getId(),requestDto);
        return ResponseEntity.ok(new CommonResponse("상품 등록이 완료되었습니다.",200,productResponseDto));
    }

    @Operation(summary = "판매자 - 상품 편집")
    @PutMapping("/edit/{productId}")
    public ResponseEntity<CommonResponse<ProductResponseDto>> editProduct(@AuthenticationPrincipal UserDetailsImpl userDetails,@Positive @PathVariable(name = "productId")Long productId,@Valid @RequestBody ProductRequestDto requestDto){
        ProductResponseDto productResponseDto = sellerProductService.editProduct(userDetails.getId(),productId,requestDto);
        return ResponseEntity.ok(new CommonResponse<>("상품 편집이 완료되었습니다.",200,productResponseDto));
    }

    @Operation(summary = "판매자 - 상품 삭제")
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<CommonResponse> deleteProduct(@AuthenticationPrincipal UserDetailsImpl userDetails,@PathVariable(name = "productId")Long productId){
        sellerProductService.deleteProduct(userDetails.getId(),productId);
        return ResponseEntity.ok(new CommonResponse("상품 삭제가 완료되었습니다.",200,""));
    }

    @Operation(summary = "판매자 - 전체 상품 조회")
    @GetMapping("/all")
    public ResponseEntity<CommonResponse<Page<ProductResponseDto>>> getAllProductOfSeller(@AuthenticationPrincipal UserDetailsImpl userDetails,@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "8")int size){
        Page<ProductResponseDto> productResponseDtos = sellerProductService.getProductsBySeller(userDetails.getId(),page-1,size);
        return ResponseEntity.ok(new CommonResponse<>("판매자의 모든 상품 조회가 완료되었습니다.",200,productResponseDtos));
    }
}
