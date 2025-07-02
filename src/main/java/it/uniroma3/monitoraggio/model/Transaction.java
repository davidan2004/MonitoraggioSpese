package it.uniroma3.monitoraggio.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

@Entity
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotNull
	@Min(1)
	private int amount;
	
	@NotNull
	@PastOrPresent
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;
	
	@NotBlank
	private String description;
	
	@NotNull
	private TransactionType type;
		
	@Enumerated(EnumType.STRING)
	@NotNull
	private TransactionCategory category;
	
	@ManyToOne
	private PaymentMethod paymentMethod;
	
	@ManyToOne
	private User user;
	
	public static int getTotalAmount(List<Transaction> transactions, TransactionType filterType) {
		int amount = 0;
		
		if(filterType == null) { 
			for(Transaction t : transactions)
				amount += t.getAmount();
		}
		else if(filterType == TransactionType.EXPENSE) {
			for(Transaction t : transactions)
				if(t.getType() == TransactionType.EXPENSE)
					amount += t.getAmount();
		} else
			for(Transaction t : transactions)
				if(t.getType() == TransactionType.PROFIT)
					amount += t.getAmount();
		
		return amount;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public int getAmount() {
		return amount;
	}


	public void setAmount(int amount) {
		this.amount = amount;
	}

	public LocalDate getDate() {
		return date;
	}


	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public TransactionCategory getCategory() {
		return category;
	}


	public void setCategory(TransactionCategory category) {
		this.category = category;
	}


	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}


	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public TransactionType getType() {
		return type;
	}

	public void setType(TransactionType type) {
		this.type = type;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	@Override
	public int hashCode() {
		return Objects.hash(amount, category, description, paymentMethod);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transaction other = (Transaction) obj;
		return amount == other.amount && Objects.equals(category, other.category)
				&& Objects.equals(description, other.description) && Objects.equals(paymentMethod, other.paymentMethod);
	}
	
}
