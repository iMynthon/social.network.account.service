package social.network.account.model.kafka;
import lombok.*;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class RegistrationEvent extends KafkaEvent {

    private String email;

    public RegistrationEvent(UUID accountId, String key,String email) {
        super(accountId, key);
        this.email = email;
    }
}
