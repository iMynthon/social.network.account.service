package social.network.account.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity(name = "accounts")
public class Account {
    @Id
    private UUID id;
    private String email;
    private String phone;
    private String photo;
    private String about;
    private String city;
    private String country;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "registration_date")
    @CreationTimestamp
    private LocalDateTime regDate;
    @Column(name = "birth_date")
    private LocalDate birthDate;
    @Column(name = "last_online_time")
    private LocalDateTime lastOnlineTime;
    @Column(name = "is_online")
    private Boolean isOnline;
    @Column(name = "is_blocked")
    private Boolean isBlocked;
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    @Column(name = "photo_name")
    private String photoName;
    @Column(name = "created_on")
    @CreationTimestamp
    private LocalDateTime createdOn;
    @Column(name = "updated_on")
    @UpdateTimestamp
    private LocalDateTime updatedOn;
    @Column(name = "emoji_status")
    private String emojiStatus;

    @PrePersist
    private void initId(){
        if (id == null) id = UUID.randomUUID();
    }
}
