import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = "http://localhost:8080";
const USER_COUNT = 100;
const SNACK_ID = 2; // 테스트할 스낵 ID
const TOKEN = "YOUR_JWT_TOKEN"; // 테스트용 토큰 (주의: 실제론 유저별로 달라야 함)

export const options = {
    // 시나리오: 100명의 유저가 동시에 딱 1번씩만 요청 (총 100건)
    scenarios: {
        buy_snack: {
            executor: 'per-vu-iterations',
            vus: USER_COUNT,
            iterations: 1, // 각 VU당 1번 실행
            maxDuration: '10s',
        },
    },
    thresholds: {
        // [중요] 카프카 방식은 일단 다 받아주므로 HTTP 실패율이 0%여야 함
        http_req_failed: ['rate==0.00'],
        // [중요] Producer가 카프카에 넣는 시간은 매우 빨라야 함 (예: 95%가 0.2초 이내)
        http_req_duration: ['p(95)<200'],
    }
};

export default function () {
    const url = `${BASE_URL}/api/orders/${SNACK_ID}`; // API 경로에 맞게 수정하세요

    const payload = JSON.stringify({
        // 필요한 경우 Body에 데이터 추가 (예: 수량)
        quantity: 1
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${TOKEN}`
        },
    };

    const res = http.post(url, payload, params);

    // [검증] 서버가 200(OK)이나 202(Accepted)를 주는지 확인
    // 이 응답은 "주문 성공"이 아니라 "주문 접수 완료"를 의미함
    check(res, {
        'status is 200 or 202': (r) => r.status === 200 || r.status === 202,
    });
}