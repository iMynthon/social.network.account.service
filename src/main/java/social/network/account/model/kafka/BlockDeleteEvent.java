package social.network.account.model.kafka;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class BlockDeleteEvent extends KafkaEvent {

    private Boolean enabled;

    public BlockDeleteEvent(UUID accountId, String key,boolean enabled) {
        super(accountId, key);
        this.enabled = enabled;
    }
}
