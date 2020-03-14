package cs4474.g9.debtledger.ui.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.data.model.UserAccount;
import cs4474.g9.debtledger.logic.ColourGenerator;
import cs4474.g9.debtledger.ui.login.LoginActivity;

public class SettingsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        setHasOptionsMenu(true);

        final LoginRepository loginRepository = LoginRepository.getInstance(getContext());
        final UserAccount user = loginRepository.getLoggedInUser();

        // Set profile picture color
        final ImageView userProfilePicture = root.findViewById((R.id.user_avatar));
        userProfilePicture.setColorFilter(ColourGenerator.generateFromName(user.getFirstName(), user.getLastName()));
        // Set profile initial
        final TextView userInitial = root.findViewById((R.id.user_initial_text_view));
        userInitial.setText(String.valueOf(user.getFirstName().charAt(0)));


        final TextInputEditText firstNameText = root.findViewById((R.id.first_name_input));
        firstNameText.setText(user.getFirstName());

        // Watch for changes to the first name field and apply them to the user account
        firstNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    user.setFirstName(charSequence.toString());
                    userProfilePicture.setColorFilter(ColourGenerator.generateFromName(user.getFirstName(), user.getLastName()));
                }
            }
        });

        final TextInputEditText lastNameText = root.findViewById((R.id.last_name_input));
        lastNameText.setText(user.getLastName());

        // Watch for changes to the last name field and apply them to the user account
        lastNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    user.setLastName(charSequence.toString());
                    userProfilePicture.setColorFilter(ColourGenerator.generateFromName(user.getFirstName(), user.getLastName()));
                }
            }
        });

        // Retrieve user email
        final TextInputEditText userEmailText = root.findViewById(R.id.user_email_text_view);
        userEmailText.setText(user.getEmail());

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                LoginRepository loginRepository = LoginRepository.getInstance(getContext());

                // Say bye
                String goodbye = getString(R.string.goodbye) + loginRepository.getLoggedInUser().getFirstName();
                Toast.makeText(getContext(), goodbye, Toast.LENGTH_LONG).show();

                loginRepository.logout(getContext());

                Intent toLogin = new Intent(getActivity(), LoginActivity.class);
                startActivity(toLogin);
                if (getActivity() != null) {
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}