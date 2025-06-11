package it.uniroma3.monitoraggio.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.monitoraggio.model.User;

public interface UserRepository extends CrudRepository<User, Long> {

	public Optional<User> findByNameAndSurname(String name, String surname);
}
