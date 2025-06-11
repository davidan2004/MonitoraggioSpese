package it.uniroma3.monitoraggio.model;

public enum TransactionType {
	EXPENSE("Spesa"), 
	PROFIT("Incasso");
	
	private final String name;
	
	TransactionType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
