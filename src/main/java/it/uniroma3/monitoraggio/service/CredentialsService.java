package it.uniroma3.monitoraggio.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.uniroma3.monitoraggio.model.Credentials;
import it.uniroma3.monitoraggio.repository.CredentialsRepository;
import jakarta.transaction.Transactional;

@Service
public class CredentialsService {

	@Autowired
	private CredentialsRepository credentialsRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	@Transactional
	public Credentials getCredentials(Long id) {
		Optional<Credentials> credentials = credentialsRepository.findById(id);
		return credentials.orElse(null);
	}
	
	@Transactional
	public Credentials getCredentials(String username) {
		Optional<Credentials> credentials = credentialsRepository.findByUsername(username);
		return credentials.orElse(null);
	}
	
	@Transactional
	public Credentials saveCredentials(Credentials credentials) {
		credentials.setRole(Credentials.DEFAULT_ROLE);
		credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
		return credentialsRepository.save(credentials);
	}
	
	public boolean isUsernameTaken(String username) {
		return credentialsRepository.existsByUsername(username);
	}
}
