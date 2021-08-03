package one.digitalinnovation.personapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PhoneNumberIsRepeatedException extends IllegalArgumentException {

    public PhoneNumberIsRepeatedException(String number) {
        super(String.format("Phone number %s is repeated!", number));
    }
    
}
