import http from 'test/http';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import { check, sleep } from 'k6';

export const options = {
    vus: 10,
    duration: '10s',
};

export function setup() {
    const vusCount = options.vus;
    const emails = [];

    for(let i=0; i<vusCount;i++){
        const email = `${randomString(18)}@example.com`;

        http.get('http://localhost:8080/', {
            headers: { 'X-Test-Member-Email': email }
        });
        emails.push(email);
    }

    return {
        emails: emails
    };
}

export default function (data) {
    const email = data.emails[__VU - 1];
    const url = 'http://localhost:8080/notes/create';
    const params = {
        headers: { 'X-Test-Member-Email': email },
        redirects: 0 // k6가 redirect 따라가는 걸 막음
    }

    const res = http.post(url, null, params);

    check(res, {
        'note create success': (r) => r.status === 302,
    });

    sleep(1);
}