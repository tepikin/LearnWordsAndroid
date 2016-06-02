package ru.lazard.learnwords.preferences;

import android.os.Bundle;

import ru.lazard.learnwords.R;

/**
 * Created by Egor on 02.06.2016.
 */
public class SettingsFragment extends android.support.v14.preference.PreferenceFragment {


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
    }
}
