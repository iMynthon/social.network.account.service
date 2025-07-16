package social.network.account.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import social.network.account.service.AccountService;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventListener {

    private final AccountService accountService;

    @KafkaListener(topics = "check-user-if-exists-request",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaAccountContainer")
    public void saveAccount(@Payload String message,
                            @Header(value = KafkaHeaders.RECEIVED_KEY,required = false) String key,
                            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                            @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                            @Header(KafkaHeaders.RECEIVED_TIMESTAMP) Long timestamp) {
        log.info("Новый зарегистрированный пользователь:\t\n {}", message);
        createMessageParameterEvent(key,topic,partition,timestamp);
        accountService.registeredSave(message);
    }

    private void createMessageParameterEvent(String key,String topic,Integer partition,Long timestamp){
        log.info("Key:{} - Topic:{} - Partition: {} - Timestamp: {}",key,topic,partition,timestamp);
    }

}
