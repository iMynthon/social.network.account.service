package social.network.account.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter @Setter
@Component
@ConfigurationProperties(prefix = "app.kafka.topics")
public class KafkaTopics {
    private String block;
    private String delete;
    private String registered;
}
