package cs4474.g9.debtledger.ui.transaction;

public class TransactionFormState {

    private Integer miscError;

    private boolean isDataValid;

    public TransactionFormState(Integer miscError) {
        this.miscError = miscError;
        this.isDataValid = false;
    }

    public TransactionFormState(boolean isDataValid) {
        this.isDataValid = isDataValid;
    }

    public Integer getMiscError() {
        return miscError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
