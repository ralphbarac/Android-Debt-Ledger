package cs4474.g9.debtledger.data.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transaction {
    private long id;
    private long debtor;
    private long creditor;
    private String description;
    private BigDecimal amount;
    private Timestamp datetime;

    public Transaction(long debtor, long creditor, String description, BigDecimal amount) {
        this.debtor = debtor;
        this.creditor = creditor;
        this.description = description;
        this.amount = amount;
    }

    public long getId() {
        return id;
    }

    public long getDebtor() {
        return debtor;
    }

    public long getCreditor() {
        return creditor;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Timestamp getDatetime() {
        return datetime;
    }
}
