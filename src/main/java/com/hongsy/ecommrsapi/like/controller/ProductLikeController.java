package com.hongsy.ecommrsapi.like.controller;

import com.hongsy.ecommrsapi.like.service.ProductLikeService;
import com.hongsy.ecommrsapi.util.UserDetailsImpl;
import com.hongsy.ecommrsapi.util.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/like")
public class ProductLikeController {
    private final ProductLikeService productLikeService;

    @PostMapping("/{productId}")
    public ResponseEntity<CommonResponse> createLike(@AuthenticationPrincipal UserDetailsImpl userDetails,@PathVariable(name = "productId") Long productId){
        productLikeService.createLike(userDetails.getUser(),productId);
        return ResponseEntity.ok(new CommonResponse<>("상품 좋아요 등록이 완료되었습니다.",200,""));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<CommonResponse> deleteLike(@AuthenticationPrincipal UserDetailsImpl userDetails,@PathVariable(name = "productId") Long productId){
        productLikeService.deleteLike(userDetails.getUser(),productId);
        return ResponseEntity.ok(new CommonResponse<>("상품 좋아요 취소가 완료되었습니다.",200,""));
    }

}
