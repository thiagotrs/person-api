package one.digitalinnovation.personapi.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import one.digitalinnovation.personapi.entities.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {
    public Optional<Person> findByCpf(String cpf);
}
