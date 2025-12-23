package com.hongsy.ecommrsapi.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

    long id() default 999L;

    String name() default "testUser";

    String email() default  "test@test.com";

    String[] roles() default {"구매자"};
}
