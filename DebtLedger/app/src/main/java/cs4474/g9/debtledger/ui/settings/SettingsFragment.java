package cs4474.g9.debtledger.ui.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import cs4474.g9.debtledger.R;
import cs4474.g9.debtledger.data.login.LoginRepository;
import cs4474.g9.debtledger.ui.login.LoginActivity;

public class SettingsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        setHasOptionsMenu(true);

        final TextView textView = root.findViewById(R.id.text_settings);
        textView.setText("This is settings fragment");
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