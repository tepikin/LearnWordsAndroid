package ru.lazard.learnwords.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

/**
 * Created by Egor on 02.06.2016.
 */
public class Settings {
    private SharedPreferences preferences;

    public Settings(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isBlinkEnable() {
        return this.preferences.getBoolean("delay_between_words_change", false);
    }

    public boolean isReadTranslate() {
        return this.preferences.getBoolean("read_translates_enable", true);
    }

    public boolean isReadWords() {
        return this.preferences.getBoolean("read_words_enable", true);
    }
}
