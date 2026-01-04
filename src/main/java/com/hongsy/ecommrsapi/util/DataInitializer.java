package com.hongsy.ecommrsapi.util;

import com.hongsy.ecommrsapi.product.entity.ColorGroup;
import com.hongsy.ecommrsapi.product.entity.Product;
import com.hongsy.ecommrsapi.user.entity.Gender;
import com.hongsy.ecommrsapi.user.entity.Role;
import com.hongsy.ecommrsapi.user.entity.User;
import com.hongsy.ecommrsapi.user.entity.UserStatus;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("local")
public class DataInitializer implements CommandLineRunner {

    private final BatchInsertRepository batchInsertRepository;
    private final JdbcTemplate jdbcTemplate;
    private final Random random = new Random();
    private final PasswordEncoder passwordEncoder;

    private static final int USER_COUNT = 10_000;
    private static final int PRODUCT_COUNT = 50_000;

    @Override
    public void run(String... args) throws Exception {
        try {
        /*System.out.println("데이터 초기화 및 ID 리셋 시작");

        jdbcTemplate.execute("TRUNCATE TABLE site_user RESTART IDENTITY CASCADE");
        System.out.println("데이터 초기화 완료 (ID 1부터 시작)");*/

            Integer userCount = jdbcTemplate.queryForObject("SELECT count(*) FROM site_user",
                Integer.class);

            if (userCount != null && userCount > 0) {
                System.out.println("이미 데이터가 존재하므로 초기화를 건너뜁니다.");
                return;
            }

            System.out.println("대용량 더미 데이터 생성 시작");
            long startTime = System.currentTimeMillis();

            List<User> users = createDummyUsers(USER_COUNT);
            batchInsertRepository.saveAllUsers(users);
            System.out.println("User " + USER_COUNT + "명 저장 완료");

            List<Product> products = createDummyProducts(PRODUCT_COUNT, USER_COUNT);
            batchInsertRepository.saveAllProducts(products);
            System.out.println("Product " + PRODUCT_COUNT + "개 저장 완료");

            long endTime = System.currentTimeMillis();
            System.out.printf("전체 데이터 적재 완료 소요 시간: %dms\n", (endTime - startTime));
        } catch (Exception e) {
            System.err.println("=========================================");
            System.err.println("데이터 초기화 중 에러 발생");
            System.err.println("=========================================");
            e.printStackTrace(); // 에러의 원인과 발생 위치 출력
            System.err.println("=========================================");
        }
    }

    private List<User> createDummyUsers(int count) {
        List<User> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            User user = User.builder()
                .email("user" + i + "@example.com")
                .password(passwordEncoder.encode("password123"))
                .name("사용자" + i)
                .age(20 + (i % 30))
                .gender(i % 2 == 0 ? Gender.Male : Gender.Female)
                .phoneNumber("010-1234-5678")
                .address("서울시 강남구 역삼동 " + i + "번지")
                .roles(Set.of(Role.ROLE_SELLER, Role.ROLE_BUYER))
                .status(UserStatus.ACTIVE)
                .build();
            list.add(user);
        }
        return list;
    }

    private List<Product> createDummyProducts(int count, int maxSellerId) {
        List<Product> list = new ArrayList<>();

        String[] keywords = {"가을", "겨울", "신상", "특가", "럭셔리", "캐주얼", "오버핏", "슬림핏", "캠핑", "여행"};
        String[] brands = {"나이키", "아디다스", "폴로", "구찌", "자라", "H&M", "유니클로", "무신사"};
        for (int i = 1; i <= count; i++) {
            Long randomSellerId = (long) (random.nextInt(maxSellerId) + 1);

            // 랜덤 키워드 조합으로 상품명 생성 (검색 품질 테스트용)
            String brand = brands[random.nextInt(brands.length)];
            String keyword = keywords[random.nextInt(keywords.length)];

            Product product = Product.builder()
                .name(brand + " " + keyword + " 상품 " + i)
                .brandName(brand)
                .info("이 상품은 " + keyword + " 테스트용 상품입니다. 번호: " + i)
                .price(BigDecimal.valueOf(10000 + (i % 100) * 1000))
                .image("https://example.com/images/product_" + i + ".jpg")
                .colorGroup(ColorGroup.getRandomColor())
                .stockQuantity(random.nextInt(500))
                .sellerId(randomSellerId)
                .tags(List.of(keyword, "인기", "추천"))
                .orderAmountFor30d(0L)
                .avgReviewScore(0.0)
                .likeCount(0)
                .build();

            list.add(product);
        }
        return list;
    }
}
