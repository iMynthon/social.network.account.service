package social.network.account.controller;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import feign.Feign;
import feign.FeignException;
import feign.RequestLine;
import feign.RetryableException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import social.network.account.config.CustomRetryer;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class CustomRetryerTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8799))
            .build();

    @Test
    void shouldRetryExactlyMaxAttemptsTimes()  {
        wireMock.stubFor(WireMock.get(anyUrl())
                .willReturn(serviceUnavailable()));
        TestClient client = Feign.builder()
                .retryer(new CustomRetryer())
                .errorDecoder((methodKey, response) -> new RetryableException(
                        response.status(),
                        "Retryable",
                        response.request().httpMethod(),
                        (Long) null,
                        response.request()
                ))
                .target(TestClient.class, wireMock.baseUrl());
        assertThrows(FeignException.class, client::testEndpoint);
        wireMock.verify(5, getRequestedFor(urlEqualTo("/test")));
    }

    interface TestClient {
        @RequestLine("GET /test")
        void testEndpoint();
    }

    @Test
    void shouldHandleInterruptedExceptionProperly() throws InterruptedException {
        CustomRetryer retryer = spy(new CustomRetryer());
        RetryableException mockException = mock(RetryableException.class);
        doThrow(new InterruptedException("Test interrupt")).when(retryer).sleep();
        retryer.continueOrPropagate(mockException);
        assertTrue(Thread.interrupted(), "Флаг прерывания должен быть сброшен");
        verify(retryer, times(1)).sleep();
    }

}
