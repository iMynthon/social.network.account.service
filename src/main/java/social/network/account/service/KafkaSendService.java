package social.network.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import social.network.account.aop.Loggable;
import social.network.account.model.kafka.BlockDeleteEvent;
import social.network.account.model.kafka.KafkaEvent;
import social.network.account.model.kafka.RegistrationEvent;
import social.network.account.utils.KafkaTopics;

@Component
@RequiredArgsConstructor
@Loggable
public class KafkaSendService {

    private final KafkaTemplate<String, KafkaEvent> kafkaTemplate;
    private final KafkaTopics kafkaTopics;

    public void sendMessage(KafkaEvent event) {
        switch (event.getKey()){
            case "block" -> block((BlockDeleteEvent) event);
            case "delete" -> delete((BlockDeleteEvent) event);
            case "registration" -> registration((RegistrationEvent) event);
        }
    }

    private void registration(RegistrationEvent event) {
        kafkaTemplate.send(kafkaTopics.getRegistered(),
                    "auth.response - " + System.currentTimeMillis(), event);
    }

    private void block(BlockDeleteEvent event){
        String key = event.getEnabled() ? "Аккаунт заблокирован" : "Аккаунт разблокирован";
        kafkaTemplate.send(kafkaTopics.getBlock(),key,event);
    }

    private void delete(BlockDeleteEvent event){
        kafkaTemplate.send(kafkaTopics.getDelete(),"Аккаунт удален",event);
    }
}
