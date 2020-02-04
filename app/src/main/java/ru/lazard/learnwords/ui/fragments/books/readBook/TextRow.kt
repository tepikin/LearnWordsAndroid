package ru.lazard.learnwords.ui.fragments.books.readBook

import ru.lazard.learnwords.model.Model
import ru.lazard.learnwords.model.Word
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class TextRow(val src: String) {
    enum class State { none, reading, readed }

    var state: State = State.none;

    var isWordsLoaded: Boolean = false;
    var isWordsLoading: Boolean = false;
    var words: List<String>? = null
    var wordsTranslated: List<Word>? = null

    var loadError:Throwable?=null

    var dst: String? = null

    fun loadWords(onStart: (() -> Unit)? = null, onEnd: (() -> Unit)? = null) {
        if (isWordsLoaded) {
            onStart?.invoke();onEnd?.invoke();return
        }
        isWordsLoading = true
        onStart?.invoke()
        try {

            dst = dst ?: Translate.translate(src)?.second



            words = words
                    ?: src.split("[\\.\\?,!\"\\W-\\(\\)]".toRegex()).map { it.trim() }.filter { it.length > 0 }.distinct()
            wordsTranslated = wordsTranslated ?: words?.map { bookWord ->
                val systemWords = Model.getInstance().words
                systemWords?.find {
                    it.translate == bookWord || it.word == bookWord
                }
                        ?: systemWords?.find {
                            it.word.startsWith(bookWord + ";")
                                    || it.translate.startsWith(bookWord + ";")
                        }
                        ?: systemWords?.find {
                            it.word.contains(bookWord + ";")
                                    || it.translate.contains(bookWord + ";")
                        }
                        ?: Word(0, null, null, bookWord)
            }
            isWordsLoading = false
            isWordsLoaded = true
            loadError=null;
        } catch (e: Throwable) {
            e.printStackTrace()
            loadError=e
            isWordsLoading = false
            isWordsLoaded = false
        }

        onEnd?.invoke()
    }


    override fun toString(): String {
        return src + "\n\n"+dst+"\n\n" + wordsTranslated?.map { "" + it.word + " -> " + it.translate }?.joinToString("\n")
    }

    fun play(function: () -> Unit) {

    }

}
