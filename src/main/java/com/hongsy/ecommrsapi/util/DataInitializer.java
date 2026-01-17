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

    private static final int TARGET_USER_COUNT = 10_000;
    private static final int TARGET_PRODUCT_COUNT = 2_000_000;

    // 메모리 보호(OOM 방지)를 위한 배치 사이즈
    private static final int BATCH_SIZE = 10_000;

    @Override
    public void run(String... args) throws Exception {
        try {
            Integer savedUserCount = jdbcTemplate.queryForObject("SELECT count(*) FROM site_user", Integer.class);
            Integer savedProductCount = jdbcTemplate.queryForObject("SELECT count(*) FROM product", Integer.class);

            int currentUserCount = savedUserCount != null ? savedUserCount : 0;
            int currentProductCount = savedProductCount != null ? savedProductCount : 0;

            System.out.println("현재 사용자 수 : " + currentUserCount);
            System.out.println("현재 상품 수 : " + currentProductCount);

            if (currentUserCount >= TARGET_USER_COUNT && currentProductCount >= TARGET_PRODUCT_COUNT) {
                System.out.println("목표 데이터 수량 달성. 초기화 건너뜀.");
                return;
            }

            System.out.println("부족한 데이터 추가 적재 시작...");
            long startTime = System.currentTimeMillis();

            while (currentUserCount < TARGET_USER_COUNT) {
                int start = currentUserCount + 1;
                int end = Math.min(currentUserCount + BATCH_SIZE, TARGET_USER_COUNT);

                List<User> users = createDummyUsers(start, end);
                batchInsertRepository.saveAllUsers(users);

                System.out.printf("User %d ~ %d 저장 완료\n", start, end);
                currentUserCount = end;
            }

            while (currentProductCount < TARGET_PRODUCT_COUNT) {
                int start = currentProductCount + 1;
                int end = Math.min(currentProductCount + BATCH_SIZE, TARGET_PRODUCT_COUNT);

                List<Product> products = createDummyProducts(start, end, TARGET_USER_COUNT);
                batchInsertRepository.saveAllProducts(products);

                System.out.printf("Product %d ~ %d 저장 완료\n", start, end);
                currentProductCount = end;
            }

            long endTime = System.currentTimeMillis();
            System.out.printf("데이터 적재 완료! 소요 시간: %dms\n", (endTime - startTime));

        } catch (Exception e) {
            System.err.println("데이터 초기화 중 에러 발생");
            e.printStackTrace();
        }
    }

    private List<User> createDummyUsers(int startIndex, int endIndex) {
        List<User> list = new ArrayList<>();

        for (int i = startIndex; i <= endIndex; i++) {
            User user = User.builder()
                .email("user" + i + "@example.com")
                .password(passwordEncoder.encode("password123"))
                .name("사용자" + i)
                .age(20 + (i % 30))
                .gender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE)
                .phoneNumber("010-1234-5678")
                .address("서울시 강남구 역삼동 " + i + "번지")
                .roles(Set.of(Role.ROLE_SELLER, Role.ROLE_BUYER))
                .status(UserStatus.ACTIVE)
                .build();
            list.add(user);
        }
        return list;
    }

    private List<Product> createDummyProducts(int startIndex, int endIndex, int maxSellerId) {
        List<Product> list = new ArrayList<>();

        String[] keywords = {
            "가을", "겨울", "봄", "여름", "신상", "특가", "럭셔리", "캐주얼",
            "오버핏", "슬림핏", "캠핑", "여행", "데일리", "포멀", "스트릿",
            "빈티지", "미니멀", "하이틴", "러블리", "청순", "힙한무드"
        };

        String[] brands = {
            "나이키", "아디다스", "폴로", "구찌", "자라", "H&M", "유니클로", "무신사",
            "스투시", "컨버스", "뉴발란스", "마르지엘라", "코스", "에잇세컨즈", "카파"
        };

        String[] extraKeywords = {
            "블랙", "화이트", "데님", "오피스룩", "홈웨어", "빅사이즈", "세트",
            "프리미엄", "베이직", "유니섹스", "트레이닝", "레이어드", "롱패딩", "숏패딩"
        };

        // info 문장 템플릿
        String[] infoTemplates = {
            "이 상품은 %s 무드의 %s 스타일 테스트용 상품입니다. 번호: %d. 데일리 코디에 활용하기 좋습니다.",
            "%s 감성의 %s 아이템으로, 시즌 한정 특가 상품입니다. 상품 코드: %d.",
            "트렌디한 %s 분위기의 %s 제품으로, 다양한 코디에 매치 가능합니다. ID: %d.",
            "%s 시즌에 어울리는 %s 상품입니다. 번호: %d. 편안한 착용감을 제공합니다."
        };

        for (int i = startIndex; i <= endIndex; i++) {
            Long randomSellerId = (long) (random.nextInt(maxSellerId) + 1);
            String brand = brands[random.nextInt(brands.length)];
            String keyword = keywords[random.nextInt(keywords.length)];
            String extra = extraKeywords[random.nextInt(extraKeywords.length)];

            // 가격, 좋아요, 리뷰, 주문량 랜덤
            BigDecimal price = BigDecimal.valueOf(10_000L + random.nextInt(191) * 1_000L);
            long orderAmountFor30d = random.nextInt(5_000);      // 0 ~ 4999
            double avgReviewScore =
                Math.round((2.5 + random.nextDouble() * 2.5) * 10.0) / 10.0; // 2.5~5.0, 소수점1자리
            int likeCount = random.nextInt(10_000);              // 0 ~ 9999

            String template = infoTemplates[random.nextInt(infoTemplates.length)];
            String info = String.format(template, keyword, brand, i);

            List<String> tags = new ArrayList<>();
            tags.add(keyword);
            tags.add(extra);
            tags.add(random.nextBoolean() ? "세일" : "신상품");
            tags.add("인기");
            tags.add("추천");

            Product product = Product.builder()
                .name(brand + " " + keyword + " " + extra + " 상품 " + i)
                .brandName(brand)
                .info(info)
                .price(price)
                .image("https://example.com/images/product_" + i + ".jpg")
                .colorGroup(ColorGroup.getRandomColor())
                .stockQuantity(random.nextInt(500))
                .sellerId(randomSellerId)
                .tags(tags)
                .orderAmountFor30d(orderAmountFor30d)
                .avgReviewScore(avgReviewScore)
                .likeCount(likeCount)
                .build();

            list.add(product);
        }
        return list;
    }
}
