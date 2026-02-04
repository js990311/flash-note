import http from 'k6/http';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';

export const options = {
    vus: 10,
    duration: '10s',
};

const mediumText = new SharedArray('medium_text_data', function () {
    const pattern = "A".repeat(1024 * 1024);
    const mediumText = pattern.repeat(15); // 15mb
    return [ { content: mediumText} ];
});

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
    const mediumTextContent = mediumText[0].content;
    const url = 'http://localhost:8080/notes/create';
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
            title: 'test-title',
            content: mediumTextContent //
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