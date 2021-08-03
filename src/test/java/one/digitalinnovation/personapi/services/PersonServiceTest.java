package one.digitalinnovation.personapi.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import one.digitalinnovation.personapi.dto.mapper.PersonMapper;
import one.digitalinnovation.personapi.dto.request.PersonDTO;
import one.digitalinnovation.personapi.dto.request.PhoneDTO;
import one.digitalinnovation.personapi.entities.Person;
import one.digitalinnovation.personapi.entities.Phone;
import one.digitalinnovation.personapi.enums.PhoneType;
import one.digitalinnovation.personapi.exception.PersonIsAlreadyExistsException;
import one.digitalinnovation.personapi.exception.PersonNotFoundException;
import one.digitalinnovation.personapi.exception.PhoneNumberIsRepeatedException;
import one.digitalinnovation.personapi.repositories.PersonRepository;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @Mock
    PersonRepository personRepository;

    @Mock
    PersonMapper personMapper;

    @InjectMocks
    PersonService personService;
    
    @Test
    void testListAll() {
        List<Person> people = Collections.singletonList(createFakePersonEntity());
        List<PersonDTO> peopleDTO = Collections.singletonList(createFakePersonDTO());

        when(personRepository.findAll()).thenReturn(people);
        when(personMapper.toDTO(any(Person.class))).thenReturn(peopleDTO.get(0));

        List<PersonDTO> expectedPeopleDTOList = personService.listAll();

        assertFalse(expectedPeopleDTOList.isEmpty());
        assertEquals(expectedPeopleDTOList.size(), peopleDTO.size());
        assertIterableEquals(expectedPeopleDTOList, peopleDTO);
    }

    @Test
    void testFindById() throws PersonNotFoundException {
        Optional<Person> personOptional = Optional.of(createFakePersonEntity());
        PersonDTO personDTO = createFakePersonDTO();

        when(personRepository.findById(personDTO.getId())).thenReturn(personOptional);
        when(personMapper.toDTO(any(Person.class))).thenReturn(personDTO);

        PersonDTO expectedPersonDTO = personService.findById(personDTO.getId());

        assertEquals(expectedPersonDTO, personDTO);
    }

    @Test
    void testFindByIncorrectId() throws PersonNotFoundException {
        when(personRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(PersonNotFoundException.class, () -> personService.findById(100L));
    }

    @Test
    void testDeleteById() throws PersonNotFoundException {
        Optional<Person> personOptional = Optional.of(createFakePersonEntity());
        PersonDTO personDTO = createFakePersonDTO();

        when(personRepository.findById(personDTO.getId())).thenReturn(personOptional);

        personService.delete(personDTO.getId());

        verify(personRepository).deleteById(personDTO.getId());
    }

    @Test
    void testDeleteByIncorrectId() throws PersonNotFoundException {
        when(personRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(PersonNotFoundException.class, () -> personService.delete(100L));
    }

    @Test
    void testCreate() {
        PersonDTO personDTO = createFakePersonDTO();
        Person person = createFakePersonEntity();

        when(personRepository.findByCpf(personDTO.getCpf())).thenReturn(Optional.empty());
        when(personMapper.toModel(personDTO)).thenReturn(person);
        when(personRepository.save(person)).thenReturn(person);

        personService.create(personDTO);

        verify(personRepository).save(person);
    }

    @Test
    void testCreateByExistingCpf() {
        Person person = createFakePersonEntity();
        PersonDTO personDTO = createFakePersonDTO();

        when(personRepository.findByCpf(personDTO.getCpf())).thenReturn(Optional.of(person));

        assertThrows(PersonIsAlreadyExistsException.class, () -> personService.create(personDTO));
    }

    @Test
    void testCreateByRepeatedPhoneNumber() {
        PersonDTO personDTO = createFakePersonDTO();
        personDTO.setPhones(List.of(createFakePhoneDTO(), createFakePhoneDTO()));

        assertThrows(PhoneNumberIsRepeatedException.class, () -> personService.create(personDTO));
    }

    @Test
    void testUpdate() throws PersonNotFoundException {
        Optional<Person> personOptional = Optional.of(createFakePersonEntity());
        PersonDTO updatedPersonDTO = createFakePersonDTO();
        Person updatedPerson = createFakePersonEntity();
        updatedPersonDTO.setLastName("Snow");
        updatedPerson.setLastName("Snow");

        when(personRepository.findById(updatedPersonDTO.getId())).thenReturn(personOptional);
        when(personMapper.toModel(updatedPersonDTO)).thenReturn(updatedPerson);
        when(personRepository.save(updatedPerson)).thenReturn(updatedPerson);

        personService.update(updatedPersonDTO.getId(), updatedPersonDTO);

        verify(personRepository).save(updatedPerson);
    }

    @Test
    void testUpdateByIncorrectId() {
        PersonDTO updatedPersonDTO = createFakePersonDTO();
        updatedPersonDTO.setLastName("Snow");

        when(personRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(PersonNotFoundException.class, () -> personService.update(100L, updatedPersonDTO));
    }

    public static PersonDTO createFakePersonDTO() {
        return PersonDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .cpf("251.988.724-90")
                .birthDate(LocalDate.of(1979, 04, 20))
                .phones(Collections.singletonList(createFakePhoneDTO()))
                .build();
    }

    public static PhoneDTO createFakePhoneDTO() {
        return PhoneDTO.builder()
                .number("21988887777")
                .type(PhoneType.MOBILE)
                .build();
    }

    public static Person createFakePersonEntity() {
        return Person.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .cpf("251.988.724-90")
                .birthDate(LocalDate.of(1979, 04, 20))
                .phones(Collections.singletonList(createFakePhoneEntity()))
                .build();
    }

    public static Phone createFakePhoneEntity() {
        return Phone.builder()
                .id(1L)
                .number("21988887777")
                .type(PhoneType.MOBILE)
                .build();
    }
}
