package cs4474.g9.debtledger.ui.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import cs4474.g9.debtledger.R;

public class PreferencesFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
