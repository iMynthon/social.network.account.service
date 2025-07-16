package social.network.account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import social.network.account.dto.response.ErrorResponse;

import javax.security.sasl.AuthenticationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResponse catchEntityNotFoundException(EntityNotFoundException efne){
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(),efne.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(AuthenticationException.class)
    public ErrorResponse catchServerErrorException(FeignRequestException fre){
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), fre.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse catchValidationException(MethodArgumentNotValidException ve){
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(),"Ошибка валидации, неправильный ввод или некорректные данные - " + ve.getBindingResult()
                .getAllErrors()
                .stream()
                .findFirst()
                .map(ObjectError::getDefaultMessage)
                .orElse("Ошибка валидации"));
    }
}
