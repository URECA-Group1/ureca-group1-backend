package urecagroup1backend.orders.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
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
public class OrderProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper; // JSON 변환기

    public void sendOrderRequest(Long snackId, Long userId) {
        OrderMessage message = new OrderMessage(userId, snackId);

        try {
            // 객체를 JSON 문자열로 변환 (직렬화)
            String jsonMessage = objectMapper.writeValueAsString(message);

            // Kafka 전송 (Key를 snackId로 설정하여 순서 보장)
            kafkaTemplate.send("snack-orders", String.valueOf(snackId), jsonMessage);

            log.info("Kafka 전송 완료: {}", jsonMessage);

        } catch (JsonProcessingException e) {
            log.error("JSON 변환 실패: {}", e.getMessage());
            throw new RuntimeException("주문 요청 처리 중 오류가 발생했습니다.");
        }
    }
}