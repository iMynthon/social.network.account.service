package social.network.account.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import social.network.account.utils.JsonCustomSerialize;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AccountResponse(
        UUID id,
        String email,
        String phone,
        String photo,
        String about,
        String city,
        String country,
        String firstName,
        String lastName,
        @JsonSerialize(using = JsonCustomSerialize.class)
        LocalDateTime regDate,
        LocalDate birthDate,
        @JsonSerialize(using = JsonCustomSerialize.class)
        LocalDateTime lastOnlineTime,
        Boolean isOnline,
        Boolean isBlocked,
        Boolean isDeleted,
        String photoName,
        @JsonSerialize(using = JsonCustomSerialize.class)
        LocalDateTime createdOn,
        @JsonSerialize(using = JsonCustomSerialize.class)
        LocalDateTime updatedOn,
        String emojiStatus
) {
}
