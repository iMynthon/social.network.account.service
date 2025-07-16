package social.network.account.dto.request;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import social.network.account.utils.JsonCustomDeserialize;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountRequest{
    private UUID id;
    private String email;
    private String phone;
    private String photo;
    private String about;
    private String city;
    private String country;
    private String firstName;
    private String lastName;
    private LocalDateTime regDate;
    @JsonDeserialize(using = JsonCustomDeserialize.class)
    private LocalDate birthDate;
    private LocalDateTime lastOnlineTime;
    private Boolean isOnline;
    private Boolean isBlocked;
    private Boolean isDeleted;
    private String photoName;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private String emojiStatus;
}
