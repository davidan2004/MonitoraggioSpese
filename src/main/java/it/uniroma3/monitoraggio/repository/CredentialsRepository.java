package it.uniroma3.monitoraggio.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.monitoraggio.model.Credentials;

public interface CredentialsRepository extends CrudRepository<Credentials, Long> {
	
	public Optional<Credentials> findByUsername(String username);
	
	public boolean existsByUsername(String username);
}
