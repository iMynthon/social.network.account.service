package social.network.account.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import social.network.account.repository.AccountRepository;
import social.network.account.utils.SecurityUtils;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@EnableScheduling
public class AccountIsOnlineStatusService implements HandlerInterceptor {

    @Getter
    private final ConcurrentHashMap<UUID, Instant> onlineUsers = new ConcurrentHashMap<>();
    private static final Duration ONLINE_THRESHOLD = Duration.ofMinutes(30);
    private final AccountRepository accountRepository;

    public AccountIsOnlineStatusService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    @Transactional
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
          UUID accountId = SecurityUtils.accountId();
          log.info("Пойман запрос от пользователя - {}",accountId);
          if(accountId != null){
              onlineUsers.put(accountId, Instant.now());
              accountRepository.setIsOnline(accountId,true);
          }
          return true;
    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void cleanupInactiveUsers() {
        log.info("Очистка неактивных пользователей");
        Instant cutoffTime = Instant.now().minus(ONLINE_THRESHOLD);
        onlineUsers.entrySet().removeIf(entry -> {
            boolean isInactive = entry.getValue().isBefore(cutoffTime);
            if(isInactive){
                accountRepository.setIsOnline(entry.getKey(),false);
            }
            return isInactive;
        });
    }
}
