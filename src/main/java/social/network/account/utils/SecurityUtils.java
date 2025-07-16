package social.network.account.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import social.network.account.security.AppUserPrincipal;

import java.util.UUID;

public final class SecurityUtils {
    public static UUID accountId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUserPrincipal appUserPrincipal = (AppUserPrincipal) authentication.getPrincipal();
        return appUserPrincipal.accountId();
    }
}
