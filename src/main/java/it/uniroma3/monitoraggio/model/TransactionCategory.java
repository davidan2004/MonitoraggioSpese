package it.uniroma3.monitoraggio.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum TransactionCategory {

    RENT("Affitto 🏠", TransactionType.EXPENSE),
    BILLS("Bollette 💡", TransactionType.EXPENSE),
    FOOD("Cibo 🍽️", TransactionType.EXPENSE),
    TRANSPORT("Trasporti 🚗", TransactionType.EXPENSE),
    LEISURE("Tempo libero 🎮", TransactionType.EXPENSE),
    CLOTHING("Abbigliamento 👕", TransactionType.EXPENSE),
    ELECTRONICS("Elettronica 💻", TransactionType.EXPENSE),

    SALARY("Stipendio 💼", TransactionType.PROFIT),
    BONUS("Bonus 🎁", TransactionType.PROFIT),
    REIMBURSEMENT("Rimborso 💸", TransactionType.PROFIT),
    SALE("Vendita 🛒", TransactionType.PROFIT);

    private final String label;
    private final TransactionType type;

    TransactionCategory(String label, TransactionType type) {
        this.label = label;
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public TransactionType getType() {
        return type;
    }
    
    public static List<TransactionCategory> getExpenseCategories() {
        return Arrays.stream(values())
                     .filter(cat -> cat.getType() == TransactionType.EXPENSE)
                     .collect(Collectors.toList());
    }

    public static List<TransactionCategory> getProfitCategories() {
        return Arrays.stream(values())
                     .filter(cat -> cat.getType() == TransactionType.PROFIT)
                     .collect(Collectors.toList());
    }
}
