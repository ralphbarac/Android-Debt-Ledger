package cs4474.g9.debtledger.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class LoginViewModelFactory implements ViewModelProvider.Factory {

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new LoginViewModel();
    }
}
