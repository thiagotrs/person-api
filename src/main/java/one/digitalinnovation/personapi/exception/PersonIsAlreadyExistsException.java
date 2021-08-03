package one.digitalinnovation.personapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PersonIsAlreadyExistsException extends IllegalArgumentException {

    public PersonIsAlreadyExistsException(String cpf) {
        super(String.format("Person with CPF %s is already exists!", cpf));
    }
    
}
