package ru.lazard.learnwords.ui.fragments.preferences;

import android.os.Bundle;


import ru.lazard.learnwords.R;
import ru.lazard.learnwords.ui.activities.main.MainActivity;

/**
 * Created by Egor on 02.06.2016.
 */
public class SettingsFragment extends 	androidx.preference.PreferenceFragmentCompat {


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        //((MainActivity)getActivity()).getFloatingActionButton().setVisibility(View.INVISIBLE);
        ((MainActivity)getActivity()).getFloatingActionButton().hide();
        ((MainActivity) getActivity()).setSelectedNavigationMenu(R.id.nav_settings);
    }

}
