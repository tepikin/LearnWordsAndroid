package ru.lazard.learnwords.ui.fragments.preferences

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import kotlin.reflect.KProperty


class Settings(val context: Context) {


    var bookReaded_isReadSrc by DelegateBoolean()
    var bookReaded_isReadDst by DelegateBoolean()
    var bookReaded_isReadSrcWordByWord by DelegateBoolean()
    var bookReaded_isReadDstWordByWord by DelegateBoolean()
    var bookReaded_isReadOnlyWords by DelegateBoolean()
    var bookReaded_isUseTranslator by DelegateBoolean()
    var bookReaded_isReadAloud by DelegateBoolean()
    var bookReaded_delayBetweenSentences_inMillisecond by DelegateInt(null,0)

    val isAutoWordsSwitch by DelegateBoolean("auto_words_switch", true)
    val isBlinkEnable by DelegateBoolean("blink_screen_enable", false)
    val isReadTranslate by DelegateBoolean("read_translates_enable", true)
    var isReadWords by DelegateBoolean("read_words_enable", true)

    var lastBookPath by DelegateString("last_book_path", null)

  //  var lastBookProgress by DelegateFloat("last_book_progress", 0f)

    fun setBookProgress(progress:Float?  = 0f,bookId:String?="") {preferences.setFloat("last_book_progress_$bookId",progress?:0f)}
    fun getBookProgress(bookId:String?="") = preferences.getFloat("last_book_progress_$bookId",0f)

    val speedReadTranslate: Float get() = preferences.getInt("translate_read_speed", 100).toFloat() / 100f

    val speedReadWords: Float get() = preferences.getInt("words_read_speed", 100).toFloat() / 100f


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
    fun SharedPreferences.setInt(key: String, value: Int) = transaction { putInt(key, value) }

    inner class DelegateBoolean(val key:String?=null,val defaultValue:Boolean=true){
        operator fun getValue(settings: Settings, property: KProperty<*>)=preferences.getBoolean(key?:property.name, defaultValue)
        operator fun setValue(settings: Settings, property: KProperty<*>, any: Any) = preferences.setBoolean(key?:property.name, any as Boolean)
    }
    inner class DelegateString(val key:String?=null,val defaultValue:String?=null){
        operator fun getValue(settings: Settings, property: KProperty<*>)=preferences.getString(key?:property.name, defaultValue)
        operator fun setValue(settings: Settings, property: KProperty<*>, any: Any) = preferences.setString(key?:property.name, any as? String)
    }
    inner class DelegateFloat(val key:String?=null,val defaultValue:Float=0f){
        operator fun getValue(settings: Settings, property: KProperty<*>)=preferences.getFloat(key?:property.name, defaultValue)
        operator fun setValue(settings: Settings, property: KProperty<*>, any: Any) = preferences.setFloat(key?:property.name, any as Float)
    }
    inner class DelegateInt(val key:String?=null,val defaultValue:Int=0){
        operator fun getValue(settings: Settings, property: KProperty<*>)=preferences.getInt(key?:property.name, defaultValue)
        operator fun setValue(settings: Settings, property: KProperty<*>, any: Any) = preferences.setInt(key?:property.name, any as Int)
    }

}
