package com.hongsy.ecommrsapi.product.repository;

import com.hongsy.ecommrsapi.product.dto.SearchRequestDto;
import com.hongsy.ecommrsapi.product.dto.SimpleProductResponseDto;
import java.util.List;

public interface ProductCustomRepository {

    List<SimpleProductResponseDto> findProductsBySearchCondition(SearchRequestDto searchRequestDto);
}
