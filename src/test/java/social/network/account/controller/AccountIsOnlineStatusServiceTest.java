package social.network.account.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import social.network.account.repository.AccountRepository;
import social.network.account.security.AppUserPrincipal;
import social.network.account.service.AccountIsOnlineStatusService;
import social.network.account.utils.SecurityUtils;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountIsOnlineStatusServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Object handler;

    @InjectMocks
    private AccountIsOnlineStatusService service;

    private final UUID testUserId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new AccountIsOnlineStatusService(accountRepository);
    }

    @Test
    void preHandle_ShouldUpdateOnlineStatus_WhenUserAuthenticated() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(new AppUserPrincipal(testUserId,"test@mail.ru"),null));
        boolean result = service.preHandle(request, response, handler);
        assertTrue(result);
        verify(accountRepository).setIsOnline(testUserId, true);
        assertTrue(service.getOnlineUsers().containsKey(testUserId));
        assertEquals(SecurityUtils.accountId(),testUserId);
    }


    @Test
    void cleanupInactiveUsers_ShouldRemoveInactiveUsers() {
        UUID activeUser = UUID.randomUUID();
        UUID inactiveUser = UUID.randomUUID();
        service.getOnlineUsers().put(activeUser, Instant.now());
        service.getOnlineUsers().put(inactiveUser, Instant.now().minus(Duration.ofHours(1)));
        service.cleanupInactiveUsers();
        assertTrue(service.getOnlineUsers().containsKey(activeUser));
        assertFalse(service.getOnlineUsers().containsKey(inactiveUser));
        verify(accountRepository).setIsOnline(inactiveUser, false);
    }

}