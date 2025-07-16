package social.network.account.dto.response;

public record ErrorResponse(
        int status,
        String message
) {
}
