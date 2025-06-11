package it.uniroma3.monitoraggio.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.monitoraggio.model.PaymentMethod;
import it.uniroma3.monitoraggio.model.PaymentType;

public interface PaymentMethodRepository extends CrudRepository<PaymentMethod, Long> {

	public Optional<PaymentMethod> findByNameAndType(String name, PaymentType type);
}
