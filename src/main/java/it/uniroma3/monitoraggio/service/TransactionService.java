package it.uniroma3.monitoraggio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.monitoraggio.model.Transaction;
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
}
