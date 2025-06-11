package it.uniroma3.monitoraggio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.monitoraggio.model.User;
import it.uniroma3.monitoraggio.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	public User getByNameAndSurname(String name, String surname) {
		return userRepository.findByNameAndSurname(name, surname).get();
	}
	
	public User getById(Long id) {
		return userRepository.findById(id).get();
	}
	
	public void save(User user) {
		userRepository.save(user);
	}
	
}
