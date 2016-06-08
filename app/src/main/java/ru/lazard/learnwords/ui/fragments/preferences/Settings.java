package ru.lazard.learnwords.ui.fragments.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class Settings {
    private SharedPreferences preferences;

    public Settings(Context context) {
//        PreferenceManager.setDefaultValues(context, R.xml.preferences,false);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Delay between words in seconds
     *
     * @return
     */
    public int delayBetweenWords() {
        String delay_between_words_change = this.preferences.getString("delay_between_words_change", "3");
        int parseInt = 3;
        try {
            parseInt = Integer.parseInt(delay_between_words_change);
        } catch (Throwable e) {
            e.printStackTrace();
            SharedPreferences.Editor edit = this.preferences.edit();
            edit.putString("delay_between_words_change","3");
            edit.commit();
        }
        return parseInt;
    }

    public boolean isAutoWordsSwitch() {
        return this.preferences.getBoolean("auto_words_switch", true);
    }

    public boolean isBlinkEnable() {
        return this.preferences.getBoolean("blink_screen_enable", false);
    }

    public boolean isReadTranslate() {
        return this.preferences.getBoolean("read_translates_enable", true);
    }

    public boolean isReadWords() {
        return this.preferences.getBoolean("read_words_enable", true);
    }

    public void setReadWords(boolean isReadWords) {
        SharedPreferences.Editor edit = this.preferences.edit();
        edit.putBoolean("read_words_enable", isReadWords);
        edit.commit();
    }

    public float speedReadTranslate() {
        int value = this.preferences.getInt("translate_read_speed", 100);
        return 1f * value / 100f;
    }

    public float speedReadWords() {
        int value = this.preferences.getInt("words_read_speed", 30);
        return 1f * value / 100f;
    }
}
