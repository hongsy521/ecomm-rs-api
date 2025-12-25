package com.hongsy.ecommrsapi.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "jwt.secret=v6yB4E9b8f2G7hN0m1p3Q5s8u9vA2d4fG6hJ8kL0m2n4p6q8r0t2v4x6z8A1B3C",
        "JWT_SECRET_KEY=v6yB4E9b8f2G7hN0m1p3Q5s8u9vA2d4fG6hJ8kL0m2n4p6q8r0t2v4x6z8A1B3C"
    })
@AutoConfigureMockMvc
public class FullIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    protected MockMvc mockMvc;
}
