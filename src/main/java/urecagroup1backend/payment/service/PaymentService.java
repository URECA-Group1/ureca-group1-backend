/*
@file PaymentService.java
@author 허영현
@version 1.0
@since 2025-12-07
@description 토스 결제 승인 요청 + 포인트 충전하는 서비스 파일입니다.
*/
package urecagroup1backend.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import urecagroup1backend.member.domain.Member;
import urecagroup1backend.member.repository.MemberRepository;
import urecagroup1backend.payment.config.PaymentClient;
import urecagroup1backend.payment.dto.PaymentConfirmRequest;
import urecagroup1backend.points.domain.Point;
import urecagroup1backend.points.repository.PointRepository;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PointRepository pointRepository;
    private final MemberRepository memberRepository;
    private final PaymentClient paymentClient;

    @Transactional
    public void confirmPayment(PaymentConfirmRequest request, Long userId) {

        // 1) Toss API 호출
        Map<String, Object> tossResponse = paymentClient.confirmPayment(request);

        // 2) status 확인
        String status = (String) tossResponse.get("status");
        if (!"DONE".equals(status)) {
            throw new IllegalStateException("결제 승인 실패: status=" + status);
        }

        // 3) 결제된 금액 가져오기 (Number로 받고 longValue()로 변환)
        Number amountNumber = (Number) tossResponse.get("totalAmount");
        if (amountNumber == null || amountNumber.longValue() <= 0) {
            throw new IllegalArgumentException("토스 응답에 totalAmount 없음");
        }
        Long amount = amountNumber.longValue();

        // 4) 포인트 충전
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보 없음"));

        Point point = pointRepository.findByMember_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("포인트 정보 없음"));

        point.addPoint(amount);
    }
}
