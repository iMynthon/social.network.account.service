package social.network.account;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import social.network.account.listener.KafkaEventListener;
import social.network.account.model.Account;
import social.network.account.openfeign.AccountFeignClient;
import social.network.account.repository.AccountRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static java.util.Arrays.asList;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class AbstractTest {

    protected final UUID FIRST_ACCOUNT = UUID.fromString("49fd1333-78d4-44f8-961b-eca8f1e2c574");

    @Autowired
    protected AccountRepository repository;

    @Autowired
    protected KafkaEventListener kafkaEventListener;

    @Autowired
    protected AccountFeignClient accountFeignClient;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MockMvc mockMvc;

    protected final String token = JWT.create()
            .withClaim("email","first@example.com")
            .withClaim("account_id", FIRST_ACCOUNT.toString())
            .sign(Algorithm.none());

    @RegisterExtension
    protected static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8790))
            .build();

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        repository.saveAll(asList(createAccount()));
        registryMockRequestFeignClientValidate();

    }

    @AfterEach
    void resetServer(){
        wireMockServer.resetAll();
    }

    private void registryMockRequestFeignClientValidate(){
        wireMockServer.stubFor(
                WireMock.get(urlPathEqualTo("/api/v1/auth/validate"))
                        .withHeader("Authorization", WireMock.equalTo("Bearer " + token))
                        .willReturn(WireMock.okJson("true"))
        );
    }

    private Account[] createAccount(){
        Account first = Account.builder()
                .id(FIRST_ACCOUNT)
                .email("first@example.com")
                .phone(null)
                .photo("https://example.com/photo.jpg")
                .about("Software developer with 5 years of experience")
                .city("New York")
                .country("USA")
                .firstName("Avraam")
                .lastName("Link")
                .regDate(LocalDateTime.of(2020, 1, 15, 10, 30))
                .birthDate(LocalDate.of(1990, 5, 20))
                .lastOnlineTime(LocalDateTime.now().minusMinutes(15))
                .isOnline(true)
                .isBlocked(false)
                .isDeleted(false)
                .photoName("profile.jpg")
                .createdOn(LocalDateTime.now().minusDays(30))
                .updatedOn(LocalDateTime.now())
                .emojiStatus("😊")
                .build();

        Account two = Account.builder()
                .id(UUID.randomUUID())
                .email("two@example.com")
                .phone("+1290347856")
                .photo("https://example.com/photo2.jpg")
                .about("Software developer with 3 years of experience")
                .city("Лондон")
                .country("Великобритания")
                .firstName("John")
                .lastName("Doe")
                .regDate(LocalDateTime.of(2023, 5, 9, 12, 30))
                .birthDate(LocalDate.of(1994, 5, 3))
                .lastOnlineTime(LocalDateTime.now().minusMinutes(90))
                .isOnline(true)
                .isBlocked(false)
                .isDeleted(false)
                .photoName("profile.jpg")
                .createdOn(LocalDateTime.now().minusDays(70))
                .updatedOn(LocalDateTime.now())
                .emojiStatus("😊")
                .build();

        Account minimalAccount = Account.builder()
                .email("minimal@example.com")
                .phone("89115678934")
                .firstName("Alice")
                .lastName("Smith")
                .birthDate(LocalDate.of(1989,6,6))
                .regDate(LocalDateTime.now())
                .isOnline(true)
                .isBlocked(false)
                .isDeleted(false)
                .build();
        return new Account[]{first,two,minimalAccount};
    }


}
