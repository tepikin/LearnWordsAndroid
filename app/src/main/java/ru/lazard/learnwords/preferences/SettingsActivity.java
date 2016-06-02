package ru.lazard.learnwords.preferences;

//import android.preference.PreferenceActivity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import ru.lazard.learnwords.R;


public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    public static void show(Context context) {
        context.startActivity(new Intent(context,SettingsActivity.class));
    }
}
