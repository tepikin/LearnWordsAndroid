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
    var speakSequence:List<String>? = null
    var wordsSrc: List<String>? = null
    var wordsSrcTranslated: List<Word>? = null
    var srcWithNewWords: String? = null
    var srcWithNewWordsList: List<String?>? = null


    var loadError:Throwable?=null

    var dst: String? = null
    var wordsDst: List<String>? = null
    var wordsDstTranslated: List<Word>? = null
    var dstWithNewWords: String? = null
    var dstWithNewWordsList: List<String?>? = null

    val wordsTranslated get()= mutableListOf<Word>().union(wordsDstTranslated?: emptyList()).union(wordsSrcTranslated?: emptyList()).distinct()


    override fun toString(): String {
        return """
            $src
            
            $srcWithNewWords
            
            $dst
            
            $dstWithNewWords
            
            ${wordsSrcTranslated?.map { "" + it.word + " -> " + it.translate }?.joinToString("\n")}
            
            ${wordsDstTranslated?.map { "" + it.word + " -> " + it.translate }?.joinToString("\n")}
            
        """.trimIndent()

    }

    fun play(function: () -> Unit) {

    }

}
