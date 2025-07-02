package it.uniroma3.monitoraggio.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import it.uniroma3.monitoraggio.model.Transaction;
import it.uniroma3.monitoraggio.model.TransactionType;
import it.uniroma3.monitoraggio.model.User;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

	List<Transaction> findByUserAndDateBetweenAndType(User user, LocalDate start, LocalDate end, TransactionType type);
	List<Transaction> findByUserAndDateBetween(User user, LocalDate start, LocalDate end);

}
