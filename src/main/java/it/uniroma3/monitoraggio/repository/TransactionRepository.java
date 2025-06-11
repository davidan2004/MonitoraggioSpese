package it.uniroma3.monitoraggio.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.uniroma3.monitoraggio.model.Transaction;
import it.uniroma3.monitoraggio.model.TransactionCategory;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {
	
	public List<Transaction> findByDateBetweenAndCategoryIn(LocalDate start, LocalDate end, TransactionCategory category);
}
