package social.network.account.openfeign;

import org.springframework.stereotype.Component;

@Component
public class AccountClientFallBack implements AccountFeignClient {

    @Override
    public Boolean validate(String token) {
        return false;
    }
}
