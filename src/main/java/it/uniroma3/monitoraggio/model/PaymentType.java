package it.uniroma3.monitoraggio.model;

public enum PaymentType {
	DEBIT_CARD("Carta di Debito 🏦"),
	CREDIT_CARD("Carta di Credito 💳"),
	CASH("Contanti 💵");
	
	private final String name;
	
	PaymentType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
}
