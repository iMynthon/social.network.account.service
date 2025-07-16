package social.network.account.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import social.network.account.dto.request.AccountRequest;
import social.network.account.exception.JsonParserRegistrationException;
import social.network.account.mapper.AccountMapper;
import social.network.account.repository.AccountRepository;
import social.network.account.service.AccountService;
import social.network.account.service.KafkaSendService;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private AccountRepository repository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private KafkaSendService sendService;

    @InjectMocks
    private AccountService accountService;

    @Test
    void testRegisteredSave_ThrowsJsonParserRegistrationException() throws JsonProcessingException {
        String invalidJson = "invalid json";
        Mockito.when(objectMapper.readValue(invalidJson,AccountRequest.class))
                .thenThrow(new JsonProcessingException("Invalid JSON") {});
        JsonParserRegistrationException exception = assertThrows(
                JsonParserRegistrationException.class,
                () -> accountService.registeredSave(invalidJson)
        );
        assertTrue(exception.getMessage().contains("Json parser exception -> AccountRequest"));
        verifyNoInteractions(accountMapper, repository, sendService);
    }
}
