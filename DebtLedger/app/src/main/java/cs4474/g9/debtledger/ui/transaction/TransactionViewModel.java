package cs4474.g9.debtledger.ui.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.model.UserAccount;

public class TransactionViewModel extends ViewModel {

    private static final Pattern DOLLAR_AMOUNT = Pattern.compile("\\d+(\\.\\d{1,2})?");

    private MutableLiveData<TransactionFormState> transactionFormState = new MutableLiveData<>();

    public TransactionViewModel() {

    }

    public LiveData<TransactionFormState> getTransactionFormState() {
        return transactionFormState;
    }

    public void transactionDataChanged(UserAccount whoIsPaying, String amountPaid, List<UserAccount> whoOwes, List<String> amountsOwed) {
        boolean isDataComplete = true;
        long amountPaidValue = 0;
        List<Long> amountOwedValues = new ArrayList<>(whoOwes == null ? 0 : whoOwes.size());

        // If either who is paying or who owes is not given, data is not complete
        if (whoIsPaying == null || whoOwes == null) {
            isDataComplete = false;
        }

        // If who is paying is given, determine if amount is in valid format
        if (whoIsPaying != null) {
            if (isAmountValid(amountPaid)) {
                amountPaidValue = parseAmountValue(amountPaid);
            } else {
                transactionFormState.setValue(new TransactionFormState(false));
                return;
            }
        }

        // If who owes is given, determine if amounts are in valid format
        if (whoOwes != null && !whoOwes.isEmpty()) {
            boolean areAnyAmountsInvalid = false;

            for (String amount : amountsOwed) {
                if (isAmountValid(amount)) {
                    amountOwedValues.add(parseAmountValue(amount));
                } else {
                    areAnyAmountsInvalid = true;
                    break;
                }
            }

            if (areAnyAmountsInvalid) {
                transactionFormState.setValue(new TransactionFormState(false));
                return;
            }
        }

        // If both who owes and who is paying is given, determine if they are equal, otherwise not complete
        if (isDataComplete) {
            long amountOwedValue = 0;
            for (Long amountOwed : amountOwedValues) {
                amountOwedValue += amountOwed;
            }

            // If amounts are equal, data is complete, otherwise return error message
            if (amountPaidValue == amountOwedValue) {
                transactionFormState.setValue(new TransactionFormState(true));
            } else {
                transactionFormState.setValue(new TransactionFormState(R.string.invalid_mismatching_amounts));
            }
        } else {
            transactionFormState.setValue(new TransactionFormState(false));
        }
    }

    public void submitTransaction() {

    }

    public static boolean isAmountValid(String value) {
        return DOLLAR_AMOUNT.matcher(value).matches();
    }

    public static long parseAmountValue(String value) {
        int indexOfDecimal = value.indexOf(".");
        if (indexOfDecimal == -1 || indexOfDecimal == value.length() - 1) {
            // If no decimal place or decimal place is last character
            value = value + "00";
        } else if (indexOfDecimal == value.length() - 2) {
            // If decimal place is second last character
            value = value + "0";
        }
        return Long.parseLong(value.replace(".", ""));
    }

}
