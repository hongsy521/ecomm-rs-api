package com.hongsy.ecommrsapi.product.repository;

import com.hongsy.ecommrsapi.product.dto.SearchRequestDto;
import com.hongsy.ecommrsapi.product.dto.SimpleProductResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ProductCustomRepository {

    Slice<SimpleProductResponseDto> findProductsBySearchCondition(SearchRequestDto searchRequestDto,
        Pageable pageable);
}
