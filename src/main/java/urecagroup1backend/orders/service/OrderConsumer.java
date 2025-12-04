package urecagroup1backend.orders.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import urecagroup1backend.orders.dto.OrderMessage;
/*
@file OrderProducer.java
@author 박서연
@version 1.0
@since 2025-12-03
@description 이 파일은 카프카기능을 수행하는 클래스입니다.
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderConsumer {

    private final OrderService orderService;
    private final ObjectMapper objectMapper; // JSON 문자열을 객체로 바꿔주는 도구

    // "snack-orders" 토픽을 감시하다가 메시지가 오면 실행됨
    @KafkaListener(topics = "snack-orders", groupId = "snack-order-group")
    public void consumeOrder(String jsonMessage) {
        try {
            log.info("Kafka 주문 수신: {}", jsonMessage);
            // 문자열(JSON) -> 자바 객체(OrderMessage)로 변환
            OrderMessage message = objectMapper.readValue(jsonMessage, OrderMessage.class);
            // DB 로직 실행
            orderService.processOrder(message.snackId(), message.userId());
        } catch (Exception e) {
            log.error("주문 처리 중 오류 발생: {}", jsonMessage, e.getMessage());
        }
    }
}