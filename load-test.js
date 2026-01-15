import http from 'k6/http';
import {check, sleep} from 'k6';

// 부하 테스트 설정 options
export const options = {
  stages: [
    {duration: '10s', target: 10}, // [Ramp-up] 10초 동안 사용자 10명까지 증가
    {duration: '30s', target: 50}, // [Load] 30초 동안 사용자 50명 유지 (동시 접속)
    {duration: '10s', target: 0},  // [Ramp-down] 10초 동안 사용자 0명으로 감소
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'], // 95%의 요청이 500ms 이내에 처리되어야 성공
  },
};

const BASE_URL = 'http://localhost:8080/api';

function randomIntBetween(min, max) {
  return Math.floor(Math.random() * (max - min + 1) + min);
}

function randomItem(arrayOfItems) {
  return arrayOfItems[Math.floor(Math.random() * arrayOfItems.length)];
}

export default function () {

  // 검색어 리스트 캐싱 효과 방지를 위해 랜덤 params
  const keywords = ["가을", "겨울", "봄", "여름", "신상", "특가", "럭셔리", "캐주얼",
    "오버핏", "슬림핏", "캠핑", "여행", "데일리", "포멀", "스트릿",
    "빈티지", "미니멀", "하이틴", "러블리", "청순", "힙한무드", "인기", "추천", "나이키", "아디다스", "폴로",
    "구찌", "자라", "H&M", "유니클로", "무신사",
    "스투시", "컨버스", "뉴발란스", "마르지엘라", "코스", "에잇세컨즈", "카파", "블랙", "BLACK",
    "화이트", "WHITE", "아이보리", "IVORY", "그레이", "GREY", "차콜", "CHARCOAL", "멜란지", "MELANGE",
    "네이비", "NAVY", "블루", "BLUE", "스카이블루", "SKY_BLUE", "데님", "DENIM",
    "베이지", "BEIGE", "오트밀", "OATMEAL", "카멜", "CAMEL", "브라운", "BROWN", "카키", "KHAKI", "레드", "RED",
    "버건디", "BURGUNDY", "핑크", "PINK", "라벤더", "LAVENDER", "그린", "GREEN", "민트", "MINT",
    "옐로우", "YELLOW", "오렌지", "ORANGE", "퍼플", "PURPLE", "멀티", "MULTI"];
  const randomKeyword = keywords[Math.floor(Math.random() * keywords.length)];

  const minPrice = randomIntBetween(10000, 90000);
  const maxPrice = minPrice + randomIntBetween(10000, 50000);

  const sortTypes = ['sales', 'review', 'like', 'price_asc', 'price_desc'];
  const sortType = randomItem(sortTypes);
  const page = randomIntBetween(1, 50);

  const searchRes = http.get(`${BASE_URL}/product`, {
    params: {
      headers: {
        'Content-Type': 'application/json',
      },
      query: {
        keyword: randomKeyword,
        page: page,
        size: 20,
        minPrice,
        maxPrice,
        sortType,
      },
    },
  });

  check(searchRes, {
    'search success': (r) => r.status === 200,
    'search duration < 300ms': (r) => r.timings.duration < 300,
  });

  // 실제 사용자는 검색 후 결과를 보느라 시간이 걸림 (1초 대기)
  sleep(1);
}