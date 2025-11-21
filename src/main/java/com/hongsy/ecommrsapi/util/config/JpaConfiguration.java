package com.hongsy.ecommrsapi.util.config;

import com.hongsy.ecommrsapi.util.ListStringToJsonConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaConfiguration {

    @Bean
    public ListStringToJsonConverter listStringToJsonConverter() {
        return new ListStringToJsonConverter();
    }
}
