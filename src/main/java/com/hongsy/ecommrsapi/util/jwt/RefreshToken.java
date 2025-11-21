package com.hongsy.ecommrsapi.util.jwt;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "refreshToken",timeToLive = 1209600)
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {
    @Id
    private String jti;
    private Long userId;
    private String token;


}
