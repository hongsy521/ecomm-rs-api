import http from 'k6/http';
import { check, sleep } from 'k6';

// 부하 테스트 설정 options
export const options = {
  stages: [
    { duration: '10s', target: 10 }, // [Ramp-up] 10초 동안 사용자 10명까지 증가
    { duration: '30s', target: 50 }, // [Load] 30초 동안 사용자 50명 유지 (동시 접속)
    { duration: '10s', target: 0 },  // [Ramp-down] 10초 동안 사용자 0명으로 감소
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
  const keywords = ['가을', '겨울', '나이키', '아디다스', '특가', '신상', '세일', '여행', '캠핑'];
  const randomKeyword = keywords[Math.floor(Math.random() * keywords.length)];

  const minPrice = randomIntBetween(10000, 90000);
  const maxPrice = minPrice + randomIntBetween(10000, 50000);

  const sortTypes = ['sales', 'review', 'like', 'price_asc', 'price_desc'];
  const sortType = randomItem(sortTypes);

  const searchRes = http.get(`${BASE_URL}/product`, {
    params: {
      headers: {
        'Content-Type': 'application/json',
      },
      query: {
        keyword: randomKeyword,
        page: 10,
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