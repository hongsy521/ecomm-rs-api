package com.hongsy.ecommrsapi.util;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(QueryDslTestConfig.class)
@AutoConfigureTestDatabase(replace = NONE)
public class RepositorySliceTest extends AbstractIntegrationTest {

}
