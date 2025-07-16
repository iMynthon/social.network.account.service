package social.network.account.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import feign.FeignException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import social.network.account.exception.FeignRequestException;
import social.network.account.openfeign.AccountFeignClient;
import social.network.account.security.AppUserPrincipal;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private static final String BEARER_PREFIX = "Bearer ";
    private final AccountFeignClient accountFeignClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
            Authentication authentication = convert(wrappedRequest);
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Authenticated: {}", authentication.getName());
            }
        filterChain.doFilter(wrappedRequest,response);
    }

    public Authentication convert(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        try {
            log.info("Проверка валидации токена");
            if (accountFeignClient.validate(token)) {
                return toAuthentication(extractBearerToken(token));
            }
            log.info("Токен не прошел валидацию, в доступен отказано");
        }catch (FeignException e){
            log.info("Ошибка отправки запроса - {}",e.getMessage());
            throw new FeignRequestException("Ошибка отправки запроса");
        }
        throw new AccessDeniedException("Ошибка аутентификации, в доступен отказано");
    }

    public Authentication toAuthentication(String token) {
        Map<String, Claim>  claims= parseTokenClaims(token);
        Claim email = claims.get("email");
        Claim accountId = claims.get("account_id");
        AppUserPrincipal principal = new AppUserPrincipal(UUID.fromString(accountId.asString()),email.asString());
        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                Collections.emptyList());
    }

    public Map<String, Claim> parseTokenClaims(String token) {
        DecodedJWT jwt = JWT.decode(token.replace(BEARER_PREFIX, ""));
        return jwt.getClaims();
    }

    private String extractBearerToken(String authorizationHeader){
        return (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) ?
                authorizationHeader.substring(BEARER_PREFIX.length()) : null;
    }

}
