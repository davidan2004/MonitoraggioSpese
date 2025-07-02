package it.uniroma3.monitoraggio.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.monitoraggio.model.Transaction;
import it.uniroma3.monitoraggio.model.TransactionType;
import it.uniroma3.monitoraggio.model.User;
import it.uniroma3.monitoraggio.repository.TransactionRepository;

@Service
public class TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;
	 
	public Transaction getById(Long id) {
		return transactionRepository.findById(id).get();
	}
	
	public void save(Transaction transaction) {
		transactionRepository.save(transaction);
	}
	
	/* For Reports */
	
	public List<Transaction> findBetweenDatesByType(User user, LocalDate start, LocalDate end, TransactionType type) {
		return transactionRepository.findByUserAndDateBetweenAndType(user, start, end, type);
	}
	
	public List<Transaction> findBetweenDates(User user, LocalDate start, LocalDate end) {
		return transactionRepository.findByUserAndDateBetween(user, start, end);
	}
}
