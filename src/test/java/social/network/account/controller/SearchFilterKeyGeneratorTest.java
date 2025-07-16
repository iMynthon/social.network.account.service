package social.network.account.controller;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import social.network.account.config.cache.CacheConfig;
import social.network.account.filter.AccountSearchDto;
import social.network.account.security.AppUserPrincipal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SearchFilterKeyGeneratorTest {

    @InjectMocks
    private CacheConfig config;

    @Test
    void testSearchFilterKeyGenerator() {
        UUID id = UUID.randomUUID();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(new AppUserPrincipal(id,"test@mail.ru"),null));
        AccountSearchDto searchDto = new AccountSearchDto();
        searchDto.setFirstName("John");
        searchDto.setLastName("Doe");
        searchDto.setIds(new UUID[]{id});
        Object[] params = new Object[]{searchDto};
        KeyGenerator keyGenerator = config.searchFilter();
        String generatedKey = keyGenerator.generate(null, null, params).toString();
        assertNotNull(generatedKey);
        assertTrue(generatedKey.contains(id.toString()));
        assertTrue(generatedKey.contains("John"));
        assertTrue(generatedKey.contains("Doe"));
        assertTrue(generatedKey.contains(id.toString()));
    }

    @Test
    void testSearchFilterKeyGenerator_ThrowsWhenNoSearchDto() {
        KeyGenerator keyGenerator = config.searchFilter();

        assertThrows(IllegalArgumentException.class,
                () -> keyGenerator.generate(null, null, new Object[]{"wrong param"}));
    }
}
