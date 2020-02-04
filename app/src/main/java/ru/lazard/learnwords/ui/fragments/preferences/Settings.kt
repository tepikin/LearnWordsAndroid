package ru.lazard.learnwords.ui.fragments.preferences

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


class Settings(val context: Context) {


    val isAutoWordsSwitch: Boolean get() = preferences.getBoolean("auto_words_switch", true)

    val isBlinkEnable: Boolean get() = preferences.getBoolean("blink_screen_enable", false)

    val isReadTranslate: Boolean get() = preferences.getBoolean("read_translates_enable", true)

    var isReadWords: Boolean
        get() = preferences.getBoolean("read_words_enable", true)
        set(isReadWords) = preferences.setBoolean("read_words_enable", isReadWords)

    var lastBookPath: String?
        get() = preferences.getString("last_book_path", null)
        set(value) = preferences.setString("last_book_path", value)

    var lastBookProgress: Float
        get() = preferences.getFloat("last_book_progress", 0f)
        set(value) = preferences.setFloat("last_book_progress", value)

    val speedReadTranslate: Float get() = 1f * preferences.getInt("translate_read_speed", 100) / 100f

    val speedReadWords: Float get() = 1f * preferences.getInt("words_read_speed", 100) / 100f


    /**
     * Delay between words in seconds
     *
     * @return
     */
    val delayBetweenWords: Int
        get() {
            val delay_between_words_change = this.preferences.getString("delay_between_words_change", "3")
            var parseInt = 3
            try {
                parseInt = Integer.parseInt(delay_between_words_change)
            } catch (e: Throwable) {
                e.printStackTrace()
                this.preferences.setString("delay_between_words_change", "3")
            }
            return parseInt
        }


    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    fun SharedPreferences.transaction(action: SharedPreferences.Editor.() -> Unit) = edit().run { action();commit();Unit }
    fun SharedPreferences.setBoolean(key: String, value: Boolean) = transaction { putBoolean(key, value) }
    fun SharedPreferences.setString(key: String, value: String?) = transaction { putString(key, value) }
    fun SharedPreferences.setFloat(key: String, value: Float) = transaction { putFloat(key, value) }

}
