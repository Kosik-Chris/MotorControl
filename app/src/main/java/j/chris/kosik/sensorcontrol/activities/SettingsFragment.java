package j.chris.kosik.sensorcontrol.activities;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import j.chris.kosik.sensorcontrol.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_pref);

    }
}
