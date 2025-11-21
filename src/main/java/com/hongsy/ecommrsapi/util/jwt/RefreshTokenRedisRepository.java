package com.hongsy.ecommrsapi.util.jwt;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken,String> {
    Optional<RefreshToken> findByToken(String token);
}
