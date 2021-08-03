package one.digitalinnovation.personapi.dto.mapper;

import org.mapstruct.Mapper;

import one.digitalinnovation.personapi.dto.request.PersonDTO;
import one.digitalinnovation.personapi.entities.Person;

@Mapper(componentModel = "spring")
public interface PersonMapper {
    
    Person toModel(PersonDTO dto);

    PersonDTO toDTO(Person person);
}
