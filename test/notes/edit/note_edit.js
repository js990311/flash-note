import http from 'k6/http';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import { check, sleep } from 'k6';
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

const textContent = open('./test-data/test7kb.txt');

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
    }
};

export function setup() {
    const email = `${randomString(18)}@example.com`;

    http.get(`${BASE_URL}`, {
        headers: { 'X-Test-Member-Email': email }
    });

    return {
        email: email,
    };
}

export default function (data) {
    const email = data.email;
    const url = `${BASE_URL}/notes/create`;
    const createRes  = http.post(url, null, {
        headers: { 'X-Test-Member-Email': email },
        redirects: 0 // k6가 redirect 따라가는 걸 막음
    });

    check(createRes, {
        'note create success': (r) => r.status === 302,
    });

    const noteEditPage = createRes.headers['Location'];
    const editRes = http.post(
        `${noteEditPage}`,{
            title: '테스트 제목은 랜덤으로 하는 것이 좋겠는가?',
            content: textContent,
            published: false
        },{
            headers: {
                'X-Test-Member-Email': email,
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            redirects: 0,
        }
    );

    check(editRes, {
        'note edit success': (r) => r.status === 302,
    });

    sleep(1);
}