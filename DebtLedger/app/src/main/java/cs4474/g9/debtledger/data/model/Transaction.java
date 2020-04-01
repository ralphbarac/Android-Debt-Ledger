package cs4474.g9.debtledger.data.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transaction {
    private String id;
    private String debtor;
    private String creditor;
    private String description;
    private BigDecimal amount;
    private Timestamp datetime;

    public Transaction(String debtor, String creditor, String desc, BigDecimal amnt) {
        this.debtor = debtor;
        this.creditor = creditor;
        this.description = desc;
        amount = amnt;
    }

    public String getID() {
        return id;
    }

    public String getDebtor() {
        return debtor;
    }

    public void setDebtor(String new_id) {
        debtor = new_id;
    }

    public String getCreditor() {
        return creditor;
    }

    public void setCreditor(String new_id) {
        creditor = new_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        description = desc;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amnt) {
        amount = amnt;
    }

    public Timestamp getDatetime() {
        return datetime;
    }
}
