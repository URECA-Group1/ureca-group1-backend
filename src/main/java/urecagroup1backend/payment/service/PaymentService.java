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
import urecagroup1backend.member.repository.MemberRepository;
import urecagroup1backend.payment.dto.PaymentConfirmRequest;
import urecagroup1backend.points.repository.PointRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PointRepository pointRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void confirmPayment(PaymentConfirmRequest request, Long userId) {

    }
}
