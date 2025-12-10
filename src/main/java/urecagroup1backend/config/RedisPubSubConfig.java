package urecagroup1backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import urecagroup1backend.meeting_room.service.MeetingRoomStatusListener;

/**
 * @file RedissonConfig
 * @author 최인호
 * @description 레디스 pub/sub 설정 파일
 */

@Configuration
public class RedisPubSubConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public ChannelTopic meetingRoomStatusTopic() {
        return new ChannelTopic("meeting-room-status");
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter meetingRoomStatusListenerAdapter,
            ChannelTopic meetingRoomStatusTopic) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // 토픽별로 리스너 등록
        container.addMessageListener(meetingRoomStatusListenerAdapter, meetingRoomStatusTopic);

        return container;
    }

    @Bean
    public MessageListenerAdapter meetingRoomStatusListenerAdapter(MeetingRoomStatusListener listener) {
        // onMessage 메소드가 호출되도록 설정
        // Serializer를 설정하지 않아야 Listener 내부의 ObjectMapper가 정상 동작
        return new MessageListenerAdapter(listener, "onMessage");
    }
}