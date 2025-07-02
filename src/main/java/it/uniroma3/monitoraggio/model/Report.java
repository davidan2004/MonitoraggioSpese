package it.uniroma3.monitoraggio.model;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;

@Entity
public class Report {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private int transactionsAmount;
	
	private int expenseAmount;
	
	private int earnedAmount;
	
	private TransactionType type;
	
	@Past
	@Column(name = "start_date")
	private LocalDate start;
	
	@NotNull
	@PastOrPresent
	@Column(name = "end_date")
	private LocalDate end;

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getExpenseAmount() {
		return expenseAmount;
	}

	public void setExpenseAmount(int expenseAmount) {
		this.expenseAmount = expenseAmount;
	}

	public int getEarnedAmount() {
		return earnedAmount;
	}

	public void setEarnedAmount(int earnedAmount) {
		this.earnedAmount = earnedAmount;
	}

	public TransactionType getType() {
		return type;
	}

	public void setType(TransactionType type) {
		this.type = type;
	}

	public LocalDate getStart() {
		return start;
	}

	public void setStart(LocalDate start) {
		this.start = start;
	}

	public LocalDate getEnd() {
		return end;
	}

	public void setEnd(LocalDate end) {
		this.end = end;
	}
	

	public int getTransactionsAmount() {
		return transactionsAmount;
	}
	
	public void setTransactionsAmount(int transactionsAmount) {
		this.transactionsAmount = transactionsAmount;
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, end, start);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Report other = (Report) obj;
		return type == other.type && Objects.equals(end, other.end) && Objects.equals(start, other.start);
	}
}
