package com.hongsy.ecommrsapi.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hongsy.ecommrsapi.product.entity.Product;
import com.hongsy.ecommrsapi.user.entity.User;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class BatchInsertRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public BatchInsertRepository(JdbcTemplate jdbcTemplate,ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper=objectMapper;
    }

    @Transactional
    public void saveAllUsers(List<User> users) {
        String sql = "INSERT INTO site_user (email, password, name, age, gender, phone_number,address, roles, status) VALUES (?, ?, ?, ?, ?, ?, ?,?::jsonb,?)";

        jdbcTemplate.batchUpdate(sql, users, 1000, (ps, user) -> {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getName());
            ps.setInt(4, user.getAge());
            ps.setString(5, user.getGender().name());
            ps.setString(6, user.getPhoneNumber());
            ps.setString(7, user.getAddress());
            // JSONB 타입 처리 (문자열로 변환된 JSON)
            try {
                String rolesJson = objectMapper.writeValueAsString(user.getRoles());
                ps.setString(8, rolesJson);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Roles convert error", e);
            }
            ps.setString(9, user.getStatus().name());
        });
    }

    @Transactional
    public void saveAllProducts(List<Product> products) {
        String sql = "INSERT INTO product (name, brand_name, info, price, image, color_group, tags, order_amount_for_30d, stock_quantity, avg_review_score, seller_id, like_count) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?::jsonb, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, products, 1000, (ps, product) -> {
            ps.setString(1, product.getName());
            ps.setString(2, product.getBrandName());
            ps.setString(3, product.getInfo());
            ps.setBigDecimal(4, product.getPrice());
            ps.setString(5, product.getImage());
            ps.setString(6, product.getColorGroup().name());
            try {
                String tagsJson = objectMapper.writeValueAsString(product.getTags());
                ps.setString(7, tagsJson);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Tags convert error", e);
            }
            ps.setLong(8, product.getOrderAmountFor30d());
            ps.setInt(9, product.getStockQuantity());
            ps.setDouble(10, product.getAvgReviewScore());
            ps.setLong(11, product.getSellerId());
            ps.setInt(12, product.getLikeCount());
        });
    }
}
