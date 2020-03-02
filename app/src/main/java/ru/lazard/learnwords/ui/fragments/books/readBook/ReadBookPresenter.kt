package ru.lazard.learnwords.ui.fragments.books.readBook

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.text.TextUtils
import android.util.Log
import ru.lazard.learnwords.model.Model
import ru.lazard.learnwords.model.Word
import ru.lazard.learnwords.speach.TTS
import ru.lazard.learnwords.ui.activities.main.MainActivity
import ru.lazard.learnwords.ui.fragments.preferences.Settings
import ru.lazard.learnwords.utils.Utils
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class ReadBookPresenter(val fragment: ReadBookFragment) {
    private val context get() = fragment.context
    private val settings by lazy { Settings(context) }
    private val tts: TTS? = (fragment?.activity as? MainActivity)?.tts
    private var isPlay: AtomicBoolean = AtomicBoolean(false)
    private var position: Int = 0
    private val currentTextRow get() = rows?.getOrNull(position)
    private val nextTextRow get() = rows?.getOrNull(position + 1)
    private val previousTextRow get() = rows?.getOrNull(position - 1)
    private var rows: List<TextRow>? = null
    var searchQuery: String? = null;
    private var bookId:String? = null;

    fun onFloatingActionButtonClick(positionIn: Int = position) {
        if (settings.bookReaded_isReadAloud) {
            if (isPlay.get()) pause() else play(positionIn)
        } else {
            play(positionIn + 1)
        }
    }

    fun pause() {
        isPlay?.set(false)
        synchronized(isPlay) {
            (isPlay as? java.lang.Object)?.notifyAll()
        }
        isPlay = AtomicBoolean(false)
        fragment.setStatePause()
        tts?.stop()
    }

    private fun play(positionIn: Int = position) {
        position = positionIn
        isPlay?.set(false)
        synchronized(isPlay) {
        (isPlay as? java.lang.Object)?.notifyAll()
    }
        isPlay = AtomicBoolean(true)
        fragment.setStatePlay()
        playStep(isPlay)
    }


    var currentRowReadProgress:Triple<TextRow?,List<String?>?,Int?>? = null

    val speechSplitRegexp = ",".toRegex()
    val readExecutor =Executors.newSingleThreadExecutor()
    private fun playStep(isPlay:AtomicBoolean) {
        if (!isPlay.get()) return
        readExecutor.submit(object : Runnable {
            override fun run() {

                val progressFloat = 1f * position / (rows?.size ?: position)
                settings.setBookProgress(progressFloat ,bookId)

                fragment?.onProgressChanged(progressFloat)

                val row = currentTextRow
                row ?: pause()
                row ?: return
                if (!isPlay.get()) return
                fragment.scrollToRow(row)
                fragment.updateRow(row);
                doSync { loadTextRowParams(row, { fragment.updateRow(row); }, { fragment.updateRow(row);countDown() }) }
                if (!isPlay.get()) return
                if (!row.isWordsLoaded) {
                    pause();return
                }
                if (!isPlay.get()) return
                nextTextRow?.let { loadTextRowParams(it, { fragment.updateRow(it); }, { fragment.updateRow(it); }) }
                if (!isPlay.get()) return
                row.state = TextRow.State.reading
                fragment.updateRow(row);
                if (!isPlay.get()) return




               val speakSequence=row.speakSequence

                var startIndex = 0;
                if (currentRowReadProgress?.first == row &&
                        (currentRowReadProgress?.second?.size?:0) == (speakSequence?.size?:0)){
                    startIndex = currentRowReadProgress?.third?:0;
                }

                try {
                    speakSequence?.forEachIndexed { index, it ->
                        if (isPlay.get() && index >= startIndex) {
                            currentRowReadProgress = Triple(row, speakSequence, index)
                            fragment.updateRow(row);
                            speekSynch(it)

                            val delay = settings.bookReaded_delayBetweenSentences_inMillisecond
                            if (delay > 0) {
                                if (!isPlay.get()) return
                                synchronized(isPlay){(isPlay as java.lang.Object).wait((delay).toLong())}
                                if (!isPlay.get()) return
                            }
                        }
                    }
                }catch (e:Throwable){
                    e.printStackTrace()
                    pause();
                    return
                }


                if (!isPlay.get()) return
                row.state = TextRow.State.readed
                fragment.updateRow(row);
                if (!isPlay.get()) return

                position++
                if (settings.bookReaded_isReadAloud) {
                    playStep(isPlay)
                }
            }
        })

    }

    private fun speekSynch(text: String?) {
        if (settings.bookReaded_isReadAloud) {
            doSync { tts?.speak(text) { countDown() } }
        }
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
        pause()
    }

    fun onResume() {

    }

    fun openStartBook(bookUri: Uri?, progress: Float?) {
        val bookUri = bookUri ?: settings.lastBookPath?.let { Uri.parse(it) }


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

    private fun openBook(bookUri: Uri, progress: Float?=0f) {
        try {
            var bookUri = bookUri;

            var text = FileToText(context).toText(bookUri);
            bookId = Utils.md5(text )
            val progress=progress?:settings.getBookProgress(bookId);
            val targetFile = File(context.getFilesDir(), "book_$bookId.txt")
            val targetUri = Uri.fromFile(targetFile)
            if (bookUri != targetUri) {
                bookUri = targetUri
                targetFile.writeText(text ?: "")
            }


//            Thread {
//                val splits = text?.split("[^\\w]".toRegex())
//                val groups = splits?.groupBy { it }
//                val sorted = groups?.map { it.key to it.value.size }?.sortedBy { it.second }?.map { it.first }?.asReversed()
//                val words = sorted?.filter { it.length>0 }?.forEach {
//                    Log.e("words",""+it);
//                    val word = textToWordObject(it)
//                    Log.e("words",""+word?.word+"->"+word?.translate);
//                }
//        }.start()

            settings.lastBookPath = bookUri.toString()
            settings.setBookProgress(progress,bookId);

            val regexWhiteSpaces = "\\s*".toRegex()
            rows = text?.replace("([\\n\\r\\.\\?!])(\\s)".toRegex(), "$1|$2")?.split("|")
//                    .filter { !it.matches(regexWhiteSpaces) }
//                    ?.toMutableList()?.let { rows ->
//                        val regex = "\\W".toRegex()
//                        val removeItems = mutableListOf<Int>()
//                        rows.forEachIndexed { index, s ->
//                        if(index>1&&s.length<10&&s.replace(regex, "").length <= 5){
//                            removeItems.add(index)
//                        }
//                        }
////                        val rowsMapped = rows.mapIndexed { index, s -> index to s }
////                        val rowsMappedFilteredLength = rowsMapped.filter{it.second.length<=10&&it.first > 1}
////                        val rowsMappedFilteredAll = rowsMappedFilteredLength.filter { it.second.replace("\\W".toRegex(), "").length <= 5 }
////                        val removeItems = rowsMappedFilteredAll.map { it.first }
//
//                        rows.forEachIndexed { index, text ->
//                            if (removeItems.contains(index )) {
//                                rows[index - 1] +=  text
//                            }
//                        }
//                        rows.filterIndexed { index, s ->  removeItems.contains(index)}
//                    }

                    ?.map { it.trim() }?.filter { it.length > 0 }?.map { TextRow(it) }

            fragment.setTextRows(rows)

            position = (progress * (rows?.size ?: 0)).toInt()
            rows?.getOrNull(position)?.let { fragment.scrollToRow(it) }

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
                                speakSequenceMutable+= (it)
                            }
                        }
                        if (settings.bookReaded_isReadDst) {
                            speakSequenceMutable += dst
                        }
                        if (settings.bookReaded_isReadDstWordByWord) {
                            speakSequenceMutable += dstWithNewWordsList?: emptyList()
                        }
                        if (settings.bookReaded_isReadOnlyWords) {
                            speakSequenceMutable += wordsTranslated?.flatMap { listOf(it.word, it.translate, " ... ") }
                        }
                        speakSequence=speakSequenceMutable.flatMap { it?.split(speechSplitRegexp)?: emptyList() }?.filterNotNull()



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


    fun getUniqueWordsFromText(sentence: String?) = sentence?.split("[\\.\\?,!\"\\s\\-\\(\\)]".toRegex())?.map { it.trim() }?.filter { it.length > 0 }?.distinct()

    fun textToWordObject(bookWord: String?): Word {
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

    var searchFromIndex = -1;
    fun onSearch(query: String) {
        if (TextUtils.isEmpty(query)){
            searchQuery = query
            searchFromIndex=-1
            return}
        if(query!=searchQuery){
            searchFromIndex=-1;
        }
        pause()
        searchQuery = query
        val row = rows?.filterIndexed { index, textRow ->index>searchFromIndex  }?.find {
            (it?.src?.contains(query, true) ?: false) ||
                    (it?.dst?.contains(query, true) ?: false) ||
                    (it?.srcWithNewWords?.contains(query, true) ?: false) ||
                    (it?.dstWithNewWords?.contains(query, true) ?: false)
        }
        searchFromIndex = rows?.indexOf(row)?:-1
        row?.let { fragment?.scrollToRow(it) }
    }

    fun onReadOrderChanged() {
        pause()
        listOf(currentTextRow, nextTextRow, previousTextRow)?.filterNotNull()?.forEach {
            it.apply {
                isWordsLoaded = false;
                dstWithNewWords = null
                dstWithNewWordsList = null

                loadError = null
                srcWithNewWords = null
                srcWithNewWordsList = null
                state = TextRow.State.none
                wordsDstTranslated = null
                wordsSrcTranslated = null
                speakSequence = null

            }
            loadTextRowParams(it, {}, { fragment.updateRow(it) })
        }

    }

    fun onBackWordButtonClick() {
        currentRowReadProgress?:return
        if ((currentRowReadProgress?.third?:0)-1<0){
            position=Math.max(0,position-1);
            currentRowReadProgress= Triple(currentTextRow,currentTextRow?.speakSequence,(currentTextRow?.speakSequence?.size?:1) )
        }
        currentRowReadProgress= Triple(currentRowReadProgress?.first,currentRowReadProgress?.second,(currentRowReadProgress?.third?:1) -1)
        pause()
        play(position)

    }

    fun onForwardWordButtonClick() {
        currentRowReadProgress?:return
        if ((currentRowReadProgress?.third?:0)+1>currentRowReadProgress?.second?.size?.minus(1)?:0){
            position=Math.max(0,position+1);
            currentRowReadProgress= Triple(currentTextRow,currentTextRow?.speakSequence,(currentTextRow?.speakSequence?.size?:-1) )
        }
        currentRowReadProgress= Triple(currentRowReadProgress?.first,currentRowReadProgress?.second,(currentRowReadProgress?.third?:-1) +1)
        pause()
        play(position)

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







