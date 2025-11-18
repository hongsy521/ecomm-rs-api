package com.hongsy.ecommrsapi.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;

@Converter(autoApply = true)
public class ListStringToJsonConverter implements AttributeConverter<List<String>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        try {
            // List<String> 객체를 JSON 문자열로 변환
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 오류: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return List.of(); // DB에 null이면 빈 목록 반환
        }
        try {
            // JSON 문자열을 List<String> 객체로 파싱
            return objectMapper.readValue(dbData, objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 파싱 오류: " + e.getMessage(), e);
        }
    }
}
