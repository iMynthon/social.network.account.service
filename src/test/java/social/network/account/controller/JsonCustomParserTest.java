package social.network.account.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import social.network.account.dto.request.AccountRequest;
import social.network.account.exception.JsonParserRegistrationException;
import social.network.account.utils.JsonCustomDeserialize;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JsonCustomParserTest {

    private final JsonCustomDeserialize deserializer = new JsonCustomDeserialize();

    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext context;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Тест -> десереализация Instant в LocalDate")
    void shouldDeserializeValidDate() throws IOException {
        String validDate = "2023-05-15T12:00:00Z";
        when(jsonParser.getText()).thenReturn(validDate);
        LocalDate result = deserializer.deserialize(jsonParser, context);
        Instant expectedInstant = Instant.parse(validDate);
        LocalDate expectedDate = LocalDate.ofInstant(expectedInstant, ZoneId.systemDefault());
        assertEquals(expectedDate, result);
    }

    @Test
    @DisplayName("Тест -> десереализация none и пустая строка в null")
    void shouldDeserializeNoneInNull() throws IOException {
        String validDate = "none";
        when(jsonParser.getText()).thenReturn(validDate);
        LocalDate result = deserializer.deserialize(jsonParser, context);
        assertNull(result);
        when(jsonParser.getText()).thenReturn(" ");
        LocalDate result1 = deserializer.deserialize(jsonParser, context);
        assertNull(result1);
    }

    @Test
    @DisplayName("Тест -> JsonParserRegistrationException при ошибке парсинга")
    void shouldThrowJsonParserRegistrationException() throws IOException {
        String invalidJson = "testJsonParser";
        when(objectMapper.readValue(invalidJson, AccountRequest.class))
                .thenThrow(new JsonParserRegistrationException("Parse error"));
        assertThrows(JsonParserRegistrationException.class, () -> {
            objectMapper.readValue(invalidJson, AccountRequest.class);
        });
    }
}
