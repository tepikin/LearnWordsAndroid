package ru.lazard.learnwords.ui.fragments.books.readBook

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import ru.lazard.learnwords.model.Model
import ru.lazard.learnwords.model.Word
import ru.lazard.learnwords.speach.TTS
import ru.lazard.learnwords.ui.activities.main.MainActivity
import ru.lazard.learnwords.ui.fragments.preferences.Settings
import ru.lazard.learnwords.utils.Utils
import java.io.File
import java.util.concurrent.CountDownLatch

class ReadBookPresenter(val fragment: ReadBookFragment) {
    private val context get() = fragment.context
    private val settings by lazy { Settings(context) }
    private val tts: TTS? = (fragment?.activity as? MainActivity)?.tts
    private var isPlay: Boolean = false
    private var position: Int = 0
    private val currentTextRow get() = rows?.getOrNull(position)
    private val nextTextRow get() = rows?.getOrNull(position + 1)
    private val previousTextRow get() = rows?.getOrNull(position - 1)
    private var rows: List<TextRow>? = null
    var searchQuery: String? = null;

    fun onFloatingActionButtonClick(positionIn: Int = position) = if (isPlay) pause() else play(positionIn)

    private fun pause() {
        isPlay = false
        fragment.setStatePause()
        tts?.stop()
    }

    private fun play(positionIn:Int=position) {
        position = positionIn
        isPlay = true
        fragment.setStatePlay()
        playStep()
    }

    private fun playStep() {
        if (!isPlay) return
        Thread(object : Runnable {
            override fun run() {

                val row = currentTextRow
                row ?: pause()
                row ?: return
                if (!isPlay) return
                fragment.scrollToRow(row)
                fragment.updateRow(row);
                doSync { loadTextRowParams(row, { fragment.updateRow(row); }, { fragment.updateRow(row);countDown() }) }
                if (!isPlay) return
                if (!row.isWordsLoaded) {
                    pause();return
                }
                if (!isPlay) return
                nextTextRow?.let { loadTextRowParams(it, { fragment.updateRow(it); }, { fragment.updateRow(it); }) }
                if (!isPlay) return
                row.state = TextRow.State.reading
                fragment.updateRow(row);
                if (!isPlay) return

                if (settings.bookReaded_isReadSrc) {
                    doSync { tts?.speak(row.src) { countDown() } }
                }
                if (!isPlay) return
                if (settings.bookReaded_isReadSrcWordByWord) {
                    row.srcWithNewWordsList?.filterNotNull()?.forEach {if (isPlay){doSync { tts?.speak(it) { countDown() } }}}
                }
                if (!isPlay) return
                if (settings.bookReaded_isReadDst) {
                    doSync { tts?.speak(row.dst) { countDown() } }
                }
                if (!isPlay) return
                if (settings.bookReaded_isReadDstWordByWord) {
                    row.dstWithNewWordsList?.filterNotNull()?.forEach {if (isPlay){doSync {tts?.speak(it) { countDown() } }}}
                }
                if (!isPlay) return
                if (settings.bookReaded_isReadOnlyWords) {
                    row.wordsTranslated?.flatMap { listOf(it.word,it.translate," ... ") } ?.forEach {if (isPlay){doSync {tts?.speak(it) { countDown() } }}}
                }
                if (!isPlay) return



                if (!isPlay) return
                row.state = TextRow.State.readed
                fragment.updateRow(row);
                if (!isPlay) return

                position++
                playStep()


            }
        }).start()

    }

    fun onDestroy() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                loadWordsHandlerThread.quitSafely()
            } else {
                loadWordsHandlerThread.quit()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun onDetach() {

    }

    fun onResume() {

    }

    fun openStartBook(bookUri: Uri?, progress: Float? = 0f) {
        val bookUri = bookUri ?: settings.lastBookPath?.let { Uri.parse(it) }
        val progress = progress ?: settings.lastBookProgress

        if (bookUri == null) {
            fragment.showFileChoicer()
            return
        }

        openBook(bookUri, progress)


    }

    fun openChosenBook(resultData: Intent?) {
        val uri = resultData?.data ?: return

        openBook(uri, 0f)

    }

    private fun openBook(bookUri: Uri, progress: Float) {
        try {
            var bookUri = bookUri;
            var text = context?.contentResolver?.openInputStream(bookUri)?.bufferedReader()?.readText()


            val targetFile = File(context.getFilesDir(), "books")
            val targetUri = Uri.fromFile(targetFile)
            if (bookUri != targetUri) {
                bookUri = targetUri
                targetFile.writeText(text ?: "")
            }



            if (text?.startsWith("<?xml") ?: false) {
                text = text?.replace("<[?/qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM][^>]*>".toRegex(), "")
            }


            settings.lastBookPath = bookUri.toString()
            settings.lastBookProgress = progress

            rows = text?.replace("([\\n\\r\\.\\?!])".toRegex(), "$1|")?.split("|")?.map { it.trim() }?.filter { it.length > 0 }?.map { TextRow(it) }
            fragment.setTextRows(rows)

            position = (progress*(rows?.size?:0)).toInt()
            rows?.getOrNull(position)?.let { fragment.scrollToRow(it)}

        } catch (e: Throwable) {
            e.printStackTrace()
            fragment.showError(e.localizedMessage)
        }


    }

    val loadWordsHandlerThread = HandlerThread("loadWordsHandlser").apply { start() }
    val loadWordsHandler: Handler? by lazy { Handler(loadWordsHandlerThread.looper) }

    fun loadTextRowParams(textRow: TextRow, onStart: (() -> Unit)? = null, onEnd: (() -> Unit)? = null) {
        loadWordsHandler?.post(object : Runnable {
            override fun run() {

                textRow?.apply {
                    if (isWordsLoaded) {
                        onStart?.invoke();onEnd?.invoke();return
                    }
                    isWordsLoading = true
                    onStart?.invoke()

                    try {

                        fun replaceSubWords(text: String?, words: List<Word>?):String? {
                            val isTextEn = Utils.isTextEn(text);
                            var text = " "+text?.toString()+" "
                            if (isTextEn) {
                                words?.distinctBy { it.word }?.filter { it.status <= 0 }?.filter { it.word != it.translate }?.forEach {
                                    try {
                                        text = text?.replace("([^\\w]${it.word})([^\\w])".toRegex(RegexOption.IGNORE_CASE), "\$1 \\(${it.word} : ${it.translate}\\)\$2")
                                    } catch (e: Throwable) {
                                        e.printStackTrace()
                                    }
                                }
                            }else{
                                words?.distinctBy { it.translate }?.filter { it.status <= 0 }?.filter { it.word != it.translate }?.forEach { try{text = text?.replace("([^\\w]${it.translate})([^\\w])".toRegex(RegexOption.IGNORE_CASE), "\$1 \\(${it.translate} : ${it.word}\\)\$2")}catch (e:Throwable){e.printStackTrace()} }
                            }
                            return text;
                        }



                        if (settings.bookReaded_isReadSrcWordByWord) {
                            wordsSrc = wordsSrc ?: getUniqueWordsFromText(src)
                            wordsSrcTranslated = wordsSrcTranslated
                                    ?: wordsSrc?.map { bookWord -> textToWordObject(bookWord) }

                            srcWithNewWords = srcWithNewWords?:replaceSubWords(src,wordsTranslated)
                            srcWithNewWordsList = srcWithNewWordsList?: srcWithNewWords?.split("[\\(:\\)]".toRegex())
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

                            dstWithNewWords = dstWithNewWords?:replaceSubWords(dst,wordsTranslated)
                            dstWithNewWordsList = dstWithNewWordsList?: dstWithNewWords?.split("[\\(:\\)]".toRegex())

                        }

                        isWordsLoading = false
                        isWordsLoaded = true
                        loadError = null;
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        loadError = e
                        isWordsLoading = false
                        isWordsLoaded = false
                    }

                    onEnd?.invoke()
                }
            }

        })
    }


    fun getUniqueWordsFromText(sentence: String?) = sentence?.split("[\\.\\?,!\"\\W-\\(\\)]".toRegex())?.map { it.trim() }?.filter { it.length > 0 }?.distinct()

    fun textToWordObject(bookWord: String?): Word {
        var bookWord = bookWord?.toLowerCase()?.trim()
        val wordsModel = Model.getInstance()
        val systemWords = wordsModel.words.filter { it.dictionaryId==6 }
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
            var translate: String? = null
            if (settings.bookReaded_isUseTranslator) {
                translate = Translate.translate(bookWord)?.second?.toLowerCase()?.trim()
            }
            systemWord = Word(6, 0, 0, null, if (isWordEn) translate else bookWord, 0, if (isWordEn) bookWord else translate)
            if (systemWord?.getWord() != null && systemWord?.getTranslate() != null) {
                wordsModel.addWord(systemWord);
            }
        }
        return systemWord
    }


    fun onSearch(query: String) {
        searchQuery = query
    }

    fun onReadOrderChanged() {
        //TODO
    }


}


/**
 * You must call <code>countDown();</code> on end of operation.
 * Other way you will wait infinity time.
 */
fun doSync(function: CountDownLatch.() -> Unit) {
    val synch = CountDownLatch(1);
    function(synch);
    synch.await()
}







