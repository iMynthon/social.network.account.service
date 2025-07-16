package social.network.account.exception;

public class FeignRequestException extends RuntimeException {
    public FeignRequestException(String message) {
        super(message);
    }
}
