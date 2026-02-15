import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  scenarios: {
    warmup_1: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '20s', target: 50 },
        { duration: '10s', target: 0 },
      ],
      startTime: '0s',
      tags: { phase: 'warmup' },
    },
    warmup_2: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '20s', target: 50 },
        { duration: '10s', target: 0 },
      ],
      startTime: '30s',
      tags: { phase: 'warmup' },
    },
    main_test_1: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: 100 },
        { duration: '10s', target: 0 },
      ],
      startTime: '60s',
      tags: { phase: 'main' },
    },
    main_test_2: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: 100 },
        { duration: '10s', target: 0 },
      ],
      startTime: '100s',
      tags: { phase: 'main' },
    },
    main_test_3: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: 100 },
        { duration: '10s', target: 0 },
      ],
      startTime: '140s',
      tags: { phase: 'main' },
    },
  },
  thresholds: {
    'http_req_duration': ['p(95)<1000', 'p(99)<2000'],
    'http_req_failed': ['rate<0.01'],
  },
};

const ALL_KEYWORDS = open('./test_keywords.txt').split('\n').filter(k => k.trim());

export function setup() {
  console.log(`총 ${ALL_KEYWORDS.length}개의 키워드 로드됨`);
  const email = `${randomString(18)}@example.com`;
  http.get(`${BASE_URL}`, {
    headers: { 'X-Test-Member-Email': email }
  });
  // 사용자 생성
  return {
    keywords: ALL_KEYWORDS,
    email: email
  };
}

export default function(data) {
  const keywordIndex = __VU % data.keywords.length;
  const email = data.email;
  const keyword = data.keywords[keywordIndex];
  const searchOption = 'TITLE_CONTENT';

  const page = 0;
  const url = `${BASE_URL}/notes/search?keyword=${encodeURIComponent(keyword)}&searchOption=${searchOption}&page=${page}`;
  const res = http.get(url, {
    headers: { 'X-Test-Member-Email': email },
  });

  check(res, {
    'status is 200': (r) => r.status === 200,
    'response time < 1s': (r) => r.timings.duration < 1000,
  });
  sleep(0.25);
}