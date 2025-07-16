package social.network.account.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountSearchDto {
    private UUID[] ids;
    private String author;
    private String firstName;
    private String lastName;
    private Instant birthDateFrom;
    private Instant birthDateTo;
    private String city;
    private String country;
    private Boolean isBlocked;
    private Boolean isDeleted;
    private Integer ageTo;
    private Integer ageFrom;
}
