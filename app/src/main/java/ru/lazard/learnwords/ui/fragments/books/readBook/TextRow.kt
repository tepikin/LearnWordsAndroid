package ru.lazard.learnwords.ui.fragments.books.readBook

import ru.lazard.learnwords.model.Model
import ru.lazard.learnwords.model.Word

class TextRow(val src: String) {
    enum class State { none, reading, readed }

    var state: State = State.none;

    var isWordsLoaded: Boolean = false;
    var isWordsLoading: Boolean = false;
    var words: List<String>? = null
    var wordsTranslated: List<Word>? = null

    fun loadWords(onStart:(()->Unit)?=null, onEnd:(()->Unit)?=null) {
        if (isWordsLoaded){onStart?.invoke();onEnd?.invoke();return}
        isWordsLoading=true
        onStart?.invoke()
        words = src.split("[\\.\\?,!\"\\W-\\(\\)]".toRegex()).map { it.trim() }.filter { it.length > 0 }.distinct()
        wordsTranslated =
                words?.map { bookWord ->
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
        isWordsLoading=false
        isWordsLoaded=true
        onEnd?.invoke()
    }


    override fun toString(): String {
        return src + "\n\n" + wordsTranslated?.map { "" + it.word + " -> " + it.translate }?.joinToString("\n")
    }

    fun play(function: () -> Unit) {

    }

}