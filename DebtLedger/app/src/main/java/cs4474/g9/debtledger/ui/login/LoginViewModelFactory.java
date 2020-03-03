package cs4474.g9.debtledger.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import cs4474.g9.debtledger.data.login.LoginRepository;

public class LoginViewModelFactory implements ViewModelProvider.Factory {

    private LoginRepository repository;

    public LoginViewModelFactory(LoginRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new LoginViewModel(repository);
    }
}
