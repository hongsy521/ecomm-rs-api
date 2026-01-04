package com.hongsy.ecommrsapi.product.entity;

import com.hongsy.ecommrsapi.user.entity.Gender;
import com.hongsy.ecommrsapi.util.exception.CustomException;
import com.hongsy.ecommrsapi.util.exception.ErrorCode;
import java.util.Arrays;
import java.util.Random;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ColorGroup {
    // 무채색 계열
    BLACK("블랙", "#000000"),
    WHITE("화이트", "#FFFFFF"),
    IVORY("아이보리", "#FFFFF0"),
    GREY("그레이", "#808080"),
    CHARCOAL("차콜", "#36454F"),
    MELANGE("멜란지", "#808080"),

    // 네이비/블루 계열
    NAVY("네이비", "#000080"),
    BLUE("블루", "#0000FF"),
    SKY_BLUE("스카이블루", "#87CEEB"),
    DENIM("데님", "#1560BD"),

    // 베이지/브라운 계열
    BEIGE("베이지", "#F5F5DC"),
    OATMEAL("오트밀", "#E0DCC8"),
    CAMEL("카멜", "#C19A6B"),
    BROWN("브라운", "#964B00"),
    KHAKI("카키", "#F0E68C"),

    // 붉은 계열
    RED("레드", "#FF0000"),
    BURGUNDY("버건디", "#800020"),
    PINK("핑크", "#FFC0CB"),
    LAVENDER("라벤더", "#E6E6FA"),

    // 기타
    GREEN("그린", "#008000"),
    MINT("민트", "#98FF98"),
    YELLOW("옐로우", "#FFFF00"),
    ORANGE("오렌지", "#FFA500"),
    PURPLE("퍼플", "#800080"),
    MULTI("멀티", "rainbow");

    private final String koreanName;
    private final String hexCode;

    private static final Random PRNG = new Random();

    public static ColorGroup getRandomColor() {
        ColorGroup[] colors = values();
        return colors[PRNG.nextInt(colors.length)];
    }

    public static ColorGroup colorGroupFromKorean(String korean){
        if(korean==null){
            throw new CustomException(ErrorCode.NULL_COLOR_GROUP);
        }
        for (ColorGroup color : ColorGroup.values()) {
            if(color.koreanName.equals(korean)){
                return color;
            }
        }
        throw new CustomException(ErrorCode.INVALID_COLOR_GROUP);
    }

    public static ColorGroup findByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return Arrays.stream(values())
            .filter(color -> color.koreanName.equals(keyword) || color.name().equalsIgnoreCase(keyword))
            .findFirst()
            .orElse(null);
    }
}
