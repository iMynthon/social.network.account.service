package social.network.account.security;

import java.security.Principal;
import java.util.UUID;

public record AppUserPrincipal(
        UUID accountId,
        String email
) implements Principal {
    @Override
    public String getName() {
        return "";
    }
}
