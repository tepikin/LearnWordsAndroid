package ru.lazard.learnwords.ui.fragments.books.readBook

import android.content.Context
import ru.lazard.learnwords.model.Model
import ru.lazard.learnwords.model.Word
import ru.lazard.learnwords.ui.fragments.preferences.Settings
import ru.lazard.learnwords.utils.Utils
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class TextRowLoader(val context: Context) {
    private val settings by lazy { Settings(context) }
    private val speechSplitRegexpBySentences = "[,.!]".toRegex()
    private val speechSplitRegexpByWords = "[, !:.]".toRegex()
    private val regexpContainsLetter = ".*\\w.*".toRegex()

    fun load(textRow:TextRow) {
        textRow?.apply {
            if (isWordsLoaded) {
                return
            }
            isWordsLoading = true

            try {

                fun replaceSubWords(text: String?, words: List<Word>?): String? {
                    val isTextEn = Utils.isTextEn(text);
                    var text = " " + text?.toString() + " "
                    if (isTextEn) {
                        words?.filter { it.word != null && it.translate != null }?.distinctBy { it.word }?.filter { it.status <= 1 }?.filter { it.word != it.translate }?.forEach {
                            try {
                                text = text?.replace("([^\\w]${it.word})([^\\w])".toRegex(RegexOption.IGNORE_CASE), "\$1 \\(${it.word} : ${it.translateShort}\\)\$2")
                            } catch (e: Throwable) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                        words?.filter { it.word != null && it.translate != null }?.distinctBy { it.translate }?.filter { it.status <= 1 }?.filter { it.word != it.translate }?.forEach {
                            try {
                                text = text?.replace("([^\\w]${it.translate})([^\\w])".toRegex(RegexOption.IGNORE_CASE), "\$1 \\(${it.translateShort} : ${it.word}\\)\$2")
                            } catch (e: Throwable) {
                                e.printStackTrace()
                            }
                        }
                    }
                    return text;
                }



                if (settings.bookReaded_isReadSrcWordByWord) {
                    wordsSrc = wordsSrc ?: getUniqueWordsFromText(src)
                    wordsSrcTranslated = wordsSrcTranslated
                            ?: wordsSrc?.map { bookWord -> textToWordObject(bookWord) }

                    srcWithNewWords = srcWithNewWords
                            ?: replaceSubWords(src, wordsTranslated)
                    srcWithNewWordsList = srcWithNewWordsList
                            ?: srcWithNewWords?.split("[\\(:\\)]".toRegex())
                }


                if (settings.bookReaded_isReadDst || settings.bookReaded_isReadDstWordByWord) {
                    if (settings.bookReaded_isUseTranslator) {
                        dst = dst ?: Translate.translate(src)?.second
                    }
                }

                if (settings.bookReaded_isReadDstWordByWord && dst != null) {
                    wordsDst = wordsDst ?: getUniqueWordsFromText(dst)
                    wordsDstTranslated = wordsDstTranslated
                            ?: wordsDst?.map { bookWord -> textToWordObject(bookWord) }

                    dstWithNewWords = dstWithNewWords
                            ?: replaceSubWords(dst, wordsTranslated)
                    dstWithNewWordsList = dstWithNewWordsList
                            ?: dstWithNewWords?.split("[\\(:\\)]".toRegex())

                }


                // generate speak sequence
                val speakSequenceMutable = mutableListOf<String?>();
                if (settings.bookReaded_isReadSrc) {
                    speakSequenceMutable += src
                }
                if (settings.bookReaded_isReadSrcWordByWord) {
                    srcWithNewWordsList?.toMutableList()?.filterNotNull()?.forEach {
                        speakSequenceMutable += (it)
                    }
                }
                if (settings.bookReaded_isReadDst) {
                    speakSequenceMutable += dst
                }
                if (settings.bookReaded_isReadDstWordByWord) {
                    speakSequenceMutable += dstWithNewWordsList ?: emptyList()
                }
                if (settings.bookReaded_isReadOnlyWords) {
                    speakSequenceMutable += wordsTranslated?.flatMap { listOf(it.word, it.translate, " ... ") }
                }
                var splitRegexp:Regex? = null;
                if (settings.bookReaded_isDelayBetweenWords) splitRegexp = speechSplitRegexpByWords
                if (settings.bookReaded_isDelayBetweenSentences) splitRegexp = speechSplitRegexpBySentences
                speakSequence = speakSequenceMutable.flatMap {if(splitRegexp==null) arrayListOf(it)else{
                    it?.split(splitRegexp) ?: emptyList()}
                }?.filterNotNull()?.map { it.trim() }?.filter { it.matches(regexpContainsLetter) }?.filterNotNull()



                isWordsLoading = false
                isWordsLoaded = true
                loadError = null;
            } catch (e: Throwable) {
                e.printStackTrace()
                loadError = e
                isWordsLoading = false
                isWordsLoaded = false
            }
        }

    }



    private fun getUniqueWordsFromText(sentence: String?) = sentence?.split("[\\.\\?,!\"\\s\\-\\(\\)]".toRegex())?.map { it.trim() }?.filter { it.length > 0 }?.distinct()

    private fun textToWordObject(bookWord: String?): Word {
        var bookWord = bookWord?.toLowerCase()?.trim()
        val wordsModel = Model.getInstance()
        val systemWords = wordsModel.words.filter { it.dictionaryId == 6 }
        val isWordEn = Utils.isTextEn(bookWord)
        var systemWord: Word? = systemWords?.find { it.translate.trim() == bookWord || it.word.trim() == bookWord }
//                ?: systemWords?.find {
//                    it.word.trim().startsWith(bookWord + ";")
//                            || it.translate.trim().startsWith(bookWord + ";")
//                }
//                ?: systemWords?.find {
//                    it.word.trim().contains(" " + bookWord + ";")
//                            || it.translate.trim().contains(" " + bookWord + ";")
//                } ?: systemWords?.find {
//                    it.word.trim().endsWith(" " + bookWord)
//                            || it.translate.trim().endsWith(" " + bookWord)
//                }
        if (systemWord == null) {


            if (settings.bookReaded_isUseTranslator) {
                systemWord = Translate.translateWordTwice(bookWord)
            } else {
                var translate: String? = null
                systemWord = Word(6, 0, 1, null, if (isWordEn) translate else bookWord, 0, if (isWordEn) bookWord else translate)
            }
            if (systemWord?.getWord() != null && systemWord?.getTranslate() != null) {
                wordsModel.addWord(systemWord);
            }
        }
        return systemWord
    }

}


