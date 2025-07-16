package social.network.account.model.kafka;
import lombok.*;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public abstract class KafkaEvent {
    private UUID accountId;
    private String key;
}