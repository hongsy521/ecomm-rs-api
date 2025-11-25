package com.hongsy.ecommrsapi.util.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Service API",
        version = "v1",
        description = "서비스의 REST API 명세"
    ),
    servers = {
        @Server(url = "/", description = "기본 서버 환경")
    }
)
public class OpenApiConfig {}
