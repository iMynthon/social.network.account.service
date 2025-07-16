package social.network.account.config.cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import social.network.account.filter.AccountSearchDto;
import social.network.account.utils.SecurityUtils;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import static social.network.account.utils.CaffeineCacheNames.*;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager springCacheManager(){
        var caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.registerCustomCache(ACCOUNT_ME,
                Caffeine.newBuilder()
                        .maximumSize(1000)
                        .expireAfterWrite(Duration.ofMinutes(30))
                        .build());
        caffeineCacheManager.registerCustomCache(ACCOUNT_ALL,
                Caffeine.newBuilder()
                        .maximumSize(5000)
                        .expireAfterWrite(Duration.ofMinutes(10))
                        .build());
        caffeineCacheManager.registerCustomCache(ACCOUNT_SEARCH_FILTER,
                Caffeine.newBuilder()
                        .maximumSize(10000)
                        .expireAfterWrite(Duration.ofMinutes(10))
                        .build());
        return caffeineCacheManager;
    }

    @Bean(name = "accountId")
    public KeyGenerator accountId(){
        return ((target, method, params) -> SecurityUtils.accountId());
    }

    @Bean(name = "searchFilter")
    public KeyGenerator searchFilter(){
        return ((target, method, params) -> {
            AccountSearchDto searchDto = Arrays.stream(params).filter(AccountSearchDto.class::isInstance)
                    .findFirst().map(AccountSearchDto.class::cast).orElseThrow(() -> new IllegalArgumentException("AccountSearchDto -> ClassNotFound"));
            int paramsHash = Objects.hash(SecurityUtils.accountId(),searchDto.getFirstName(),searchDto.getLastName(),Arrays.toString(searchDto.getIds()));
            return String.format("#%s#%s#%s#%s#%d",SecurityUtils.accountId(),searchDto.getFirstName(),searchDto.getLastName(),Arrays.toString(searchDto.getIds()),paramsHash);
        });
    }

}
