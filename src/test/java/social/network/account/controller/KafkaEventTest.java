package social.network.account.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import social.network.account.AbstractTest;
import social.network.account.TestContainerInitializer;
import social.network.account.dto.request.AccountRequest;
import social.network.account.model.kafka.KafkaEvent;
import social.network.account.model.kafka.RegistrationEvent;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ContextConfiguration(initializers = TestContainerInitializer.class)
class KafkaEventTest extends AbstractTest {

    @MockBean
    private KafkaTemplate<String, KafkaEvent> kafkaTemplateEvent;

    @Test
    @DisplayName("Test на сохранение аккаунта и отправку сообщения")
    @Transactional(propagation = Propagation.SUPPORTS)
    void testSaveAccount() throws JsonProcessingException {
        AccountRequest request = AccountRequest.builder()
                .email("accounts@mail.ru")
                .firstName("test")
                .lastName("test")
                .phone("9112345678").build();
        String message = objectMapper.writeValueAsString(request);
        assertEquals(3,repository.count());
        kafkaEventListener.saveAccount(message,"registration","check-user-if-exists-request",0,null);
        assertEquals(4,repository.count());
        verify(kafkaTemplateEvent, times(1))
                .send(anyString(), anyString(), any(KafkaEvent.class));
        ArgumentCaptor<KafkaEvent> eventCaptor = ArgumentCaptor.forClass(KafkaEvent.class);
        verify(kafkaTemplateEvent).send(anyString(), anyString(), eventCaptor.capture());

        KafkaEvent sentEvent = eventCaptor.getValue();
        assertInstanceOf(RegistrationEvent.class, sentEvent);
        assertEquals("accounts@mail.ru", ((RegistrationEvent)sentEvent).getEmail());
    }
}
