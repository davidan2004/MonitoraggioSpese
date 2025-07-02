package it.uniroma3.monitoraggio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.monitoraggio.controller.GlobalController;
import it.uniroma3.monitoraggio.model.Credentials;
import it.uniroma3.monitoraggio.model.User;
import it.uniroma3.monitoraggio.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private GlobalController globalController;
	
	@Autowired
	private CredentialsService credentialsService;
	
	public User getByNameAndSurname(String name, String surname) {
		return userRepository.findByNameAndSurname(name, surname).get();
	}
	
	public User getById(Long id) {
		return userRepository.findById(id).get();
	}
	
	public void save(User user) {
		userRepository.save(user);
	}
	
	public User getCurrentUser() {
		String username = globalController.getUser().getUsername();
		Credentials credentials = credentialsService.getCredentials(username);
		
		if(credentials == null)
			return null;
		return credentials.getUser();
	}
	
}
