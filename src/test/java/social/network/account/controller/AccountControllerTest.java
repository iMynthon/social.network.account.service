package social.network.account.controller;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.github.tomakehurst.wiremock.client.WireMock;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import social.network.account.AbstractTest;
import social.network.account.TestContainerInitializer;
import social.network.account.dto.request.AccountRequest;
import social.network.account.filter.AccountSearchDto;
import social.network.account.model.Account;
import social.network.account.repository.projections.IdProjections;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.UUID;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ContextConfiguration(initializers = TestContainerInitializer.class)
class AccountControllerTest extends AbstractTest {

    @Test
    @DisplayName("Тест на получения своего аккаунта")
    void testGetAccountMe() throws Exception{
        IdProjections id = repository.findByEmail("first@example.com").orElseThrow();
        mockMvc.perform(get("/api/v1/account/me")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(id.getId().toString()))
                .andExpect(jsonPath("$.email").value("first@example.com"));
    }

    @Test
    @DisplayName("Тест сервер недоступен")
    void testFeignRequestException() {
        wireMockServer.stubFor(
                WireMock.get(WireMock.urlPathEqualTo("/api/v1/auth/validate"))
                        .withHeader("Authorization", WireMock.equalTo("Bearer " + token))
                        .willReturn(WireMock.aResponse()
                                .withStatus(503)
                                .withHeader("Content-Type", "application/json")
                                .withBody("Ошибка отправки запроса"))
        );
        FeignException exception = assertThrows(
                FeignException.class,
                () -> accountFeignClient.validate("Bearer " + token)
        );
        assertTrue(exception.getMessage().contains("Ошибка отправки запроса"));
        assertTrue(exception.getMessage().contains("503 Service Unavailable"));
    }


    @Test
    @DisplayName("Тест EntityNotFoundException")
    void testEntityNotFoundException() throws Exception{
        String token = JWT.create()
                .withClaim("email","random@example.com")
                .withClaim("account_id", UUID.randomUUID().toString())
                .sign(Algorithm.none());
        wireMockServer.stubFor(
                WireMock.get(urlPathEqualTo("/api/v1/auth/validate"))
                        .withHeader("Authorization", WireMock.equalTo("Bearer " + token))
                        .willReturn(WireMock.okJson("true"))
        );
        mockMvc.perform(get("/api/v1/account/me")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("Тест на обновление аккаунта")
    void testUpdateAccountMe() throws Exception{
        AccountRequest request = AccountRequest.builder()
                .email("test@mail.com")
                .phone("")
                .build();

        mockMvc.perform(put("/api/v1/account/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                .header("Authorization","Bearer " + token))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @DisplayName("Тест на удаление аккаунта")
    @Transactional(propagation = Propagation.SUPPORTS)
    void testBlockAndDeleteAccounts() throws Exception{

        IdProjections id = repository.findByEmail("first@example.com").orElseThrow();

        String result = mockMvc.perform(delete("/api/v1/account/me")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals("Аккаунт удален", result);

        Account account = repository.findById(id.getId()).orElseThrow();
        assertEquals(true,account.getIsDeleted());
    }

    @Test
    @DisplayName("Test на блокирование аккаунта и разблокирование")
    @Transactional(propagation = Propagation.SUPPORTS)
    void testBlockAndUnblockAccount() throws Exception{

        IdProjections id = repository.findByEmail("first@example.com").orElseThrow();

        String block = mockMvc.perform(delete("/api/v1/account/block/{id}",id.getId())
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("Аккаунт заблокирован",block);

        Account accountBlock = repository.findById(id.getId()).orElseThrow();

        assertEquals("first@example.com",accountBlock.getEmail());
        assertEquals(true,accountBlock.getIsBlocked());

        String unblock = mockMvc.perform(put("/api/v1/account/block/{id}",id.getId())
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Account accountUnblock = repository.findById(id.getId()).orElseThrow();

        assertEquals("Аккаунт разблокирован",unblock);
        assertEquals("first@example.com",accountBlock.getEmail());
        assertEquals(false,accountUnblock.getIsBlocked());
    }

    @Test
    @DisplayName("Тест на запрос всех аккаунтов без фильтрации по полям")
    void testGetAllAccounts() throws Exception{
        mockMvc.perform(get("/api/v1/account")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].id").isNotEmpty())
                .andExpect(jsonPath("$.content.[1].id").isNotEmpty())
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andDo(print());
    }

    @Test
    @DisplayName("Тест поиск аккаунта по id")
    void testFindByIdAccount() throws Exception{
        IdProjections id = repository.findByEmail("first@example.com").orElseThrow();

        mockMvc.perform(get("/api/v1/account/{id}",id.getId())
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.getId().toString()))
                .andExpect(jsonPath("$.email").value("first@example.com"));
    }



    @Test
    @DisplayName("Тест поиск по LocalDateTime birthDateFrom и birthDateTo")
    void testSearchAccountByBirthdayLocalDateTime() throws Exception{
        LocalDate birthDate = LocalDate.of(1800, 10, 10);
        LocalDate currentDate = LocalDate.of(2024, 10, 10);

        String birthDateFrom = birthDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toString();
        String birthDateTo = currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toString();

        mockMvc.perform(get(String.format("/api/v1/account/search?city=Лондон&birthDateFrom=%s&birthDateTo=%s",birthDateFrom,birthDateTo))
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].city").value("Лондон"))
                .andExpect(jsonPath("$.content.[0].birthDate").isNotEmpty())
                .andDo(print());

        mockMvc.perform(get(String.format("/api/v1/account/search?city=Лондон&birthDateTo=%s",birthDateTo))
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].city").value("Лондон"))
                .andExpect(jsonPath("$.content.[0].birthDate").isNotEmpty())
                .andDo(print());

        mockMvc.perform(get(String.format("/api/v1/account/search?city=Лондон&birthDateFrom=%s",birthDateFrom))
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].city").value("Лондон"))
                .andExpect(jsonPath("$.content.[0].birthDate").isNotEmpty())
                .andDo(print());
    }


    @Test
    @DisplayName("Тест поиск аккаунта с фильтрацией по полям")
    void testSearchAccountByFilter() throws Exception{
        mockMvc.perform(get("/api/v1/account/search?city=Лондон&ageFrom=10&ageTo=65")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].city").value("Лондон"))
                .andExpect(jsonPath("$.content.[0].birthDate").isNotEmpty())
                .andDo(print());
        mockMvc.perform(get("/api/v1/account/search?city=Лондон&ageFrom=10")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].city").value("Лондон"))
                .andExpect(jsonPath("$.content.[0].birthDate").isNotEmpty())
                .andDo(print());
        mockMvc.perform(get("/api/v1/account/search?city=Лондон&country=Великобритания&ageTo=65")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].city").value("Лондон"))
                .andExpect(jsonPath("$.content.[0].birthDate").isNotEmpty())
                .andDo(print());

        mockMvc.perform(get("/api/v1/account/search?author=Alice")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].firstName").value("Alice"))
                .andExpect(jsonPath("$.content.[0].city").isEmpty())
                .andExpect(jsonPath("$.content.[0].birthDate").isNotEmpty())
                .andDo(print());

        mockMvc.perform(get("/api/v1/account/search?firstName=Alice&lastName=Smith")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].firstName").value("Alice"))
                .andExpect(jsonPath("$.content.[0].lastName").value("Smith"))
                .andExpect(jsonPath("$.content.[0].city").isEmpty())
                .andExpect(jsonPath("$.content.[0].birthDate").isNotEmpty())
                .andDo(print());

        mockMvc.perform(get("/api/v1/account/search?isDeleted=true")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andDo(print());

        mockMvc.perform(get("/api/v1/account/search?isBlocked=true")
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andDo(print());
    }

    @Test
    @DisplayName("Тест поиск по полю ids")
    void testSearchIds() throws Exception{
        IdProjections id = repository.findByEmail("first@example.com").orElseThrow();
        IdProjections id2 = repository.findByEmail("two@example.com").orElseThrow();
        mockMvc.perform(get("/api/v1/account/accountIds?")
                        .param("ids",id.getId().toString(),id2.getId().toString())
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.[0].id").isNotEmpty())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andDo(print());
    }

    @Test
    @DisplayName("Тест поиск по ids")
    void testFindAccountsIds() throws Exception{
        IdProjections id = repository.findByEmail("first@example.com").orElseThrow();
        IdProjections id2 = repository.findByEmail("two@example.com").orElseThrow();
        mockMvc.perform(post("/api/v1/account/find")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Arrays.asList(id.getId(),id2.getId())))
                        .header("Authorization","Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]").isNotEmpty())
                .andDo(print());
    }

    @Test
    @DisplayName("Тест findByFilter")
    void testSearchByFilter() throws Exception{
        AccountSearchDto filter = AccountSearchDto.builder().city("Лондон")
                .ageFrom(10)
                .ageTo(65)
                .build();
        mockMvc.perform(post("/api/v1/account/searchByFilter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter))
                        .header("Authorization","Bearer " + token))
                .andExpect(jsonPath("$.content.[0].city").value("Лондон"))
                .andExpect(jsonPath("$.content.[0].birthDate").isNotEmpty())
                .andExpect(status().isOk());
    }
}
