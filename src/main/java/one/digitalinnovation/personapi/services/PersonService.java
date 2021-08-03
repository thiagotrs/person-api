package one.digitalinnovation.personapi.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import one.digitalinnovation.personapi.dto.mapper.PersonMapper;
import one.digitalinnovation.personapi.dto.request.PersonDTO;
import one.digitalinnovation.personapi.entities.Person;
import one.digitalinnovation.personapi.exception.PersonIsAlreadyExistsException;
import one.digitalinnovation.personapi.exception.PersonNotFoundException;
import one.digitalinnovation.personapi.exception.PhoneNumberIsRepeatedException;
import one.digitalinnovation.personapi.repositories.PersonRepository;

@Service
public class PersonService {
    
    private PersonRepository personRepository;
    private PersonMapper personMapper;

    public PersonService(PersonRepository personRepository, PersonMapper personMapper) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
    }

    public List<PersonDTO> listAll() {
        List<Person> people = personRepository.findAll();
        return people.stream().map(personMapper::toDTO).collect(Collectors.toList());
    }

    public PersonDTO findById(Long id) throws PersonNotFoundException {
        Person person = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
        return personMapper.toDTO(person);
    }

    public void delete(Long id) throws PersonNotFoundException {
        personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
        personRepository.deleteById(id);
    }

    public void create(PersonDTO dto) {
        dto.getPhones().stream().map(phone -> phone.getNumber())
            .collect(Collectors.groupingBy(k -> k, Collectors.counting()))
            .entrySet().stream().filter(m -> m.getValue() > 1).findAny().ifPresent(nPhone -> {
                throw new PhoneNumberIsRepeatedException(nPhone.getKey());
            });
        
        personRepository.findByCpf(dto.getCpf()).ifPresent(person -> {
            throw new PersonIsAlreadyExistsException(person.getCpf());
        });

        Person person = personMapper.toModel(dto);
        personRepository.save(person);
    }

    public void update(Long id, PersonDTO dto) throws PersonNotFoundException {
        personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException(id));
        Person updatedPerson = personMapper.toModel(dto);
        personRepository.save(updatedPerson);
    }
}
