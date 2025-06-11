package it.uniroma3.monitoraggio.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.monitoraggio.model.PaymentMethod;
import it.uniroma3.monitoraggio.model.PaymentType;
import it.uniroma3.monitoraggio.repository.PaymentMethodRepository;

@Service
public class PaymentMethodService {

	@Autowired
	private PaymentMethodRepository paymentMethodRepository;
	
	public PaymentMethod getByNameAndType(String name, PaymentType type) {
		return paymentMethodRepository.findByNameAndType(name, type).get();
	}
	
	public PaymentMethod getById(Long id) {
		return paymentMethodRepository.findById(id).get();
	}
	
	public void save(PaymentMethod paymentMethod) {
		paymentMethodRepository.save(paymentMethod);
	}
	
	public void removeById(Long id) {
		paymentMethodRepository.deleteById(id);
	}
}
