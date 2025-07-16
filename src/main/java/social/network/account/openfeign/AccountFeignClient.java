package social.network.account.openfeign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;


@FeignClient(name = "auth",
        fallback = AccountClientFallBack.class)
public interface AccountFeignClient {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/api/v1/auth/validate")
    Boolean validate(@RequestHeader(name = "Authorization") String token);
}
