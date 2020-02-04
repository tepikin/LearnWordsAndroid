package ru.lazard.learnwords.ui.fragments.books.readBook

import android.content.Intent
import android.net.Uri
import ru.lazard.learnwords.speach.TTS
import ru.lazard.learnwords.ui.activities.main.MainActivity
import ru.lazard.learnwords.ui.fragments.preferences.Settings
import java.util.*
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

    fun onFloatingActionButtonClick() = if (isPlay) pause() else play()

    private fun pause() {
        isPlay = false
        fragment.setStatePause()
        tts?.stop()
    }

    private fun play() {
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

                fragment.scrollToRow(row)
                fragment.updateRow(row);
                doSync { row.loadWords({ fragment.updateRow(row); }, { fragment.updateRow(row);countDown() }) }

                if (!row.isWordsLoaded) {pause();return}

                nextTextRow?.let { it.loadWords({ fragment.updateRow(it); }, { fragment.updateRow(it); }) }

                row.state = TextRow.State.reading
                fragment.updateRow(row);
                doSync { tts?.speak(row.src) { countDown() } }


                row.state = TextRow.State.reading
                fragment.updateRow(row);
                doSync { tts?.speak(row.dst) { countDown() } }

                row.state = TextRow.State.readed
                fragment.updateRow(row);


                position++
                playStep()


            }
        }).start()

    }

    fun onDestroy() {

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
            var text = context?.contentResolver?.openInputStream(bookUri)?.bufferedReader()?.readText()
            if (text?.startsWith("<?xml") ?: false) {
                text = text?.replace("<[?/qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM][^>]*>".toRegex(), "")
            }


            settings.lastBookPath = bookUri.toString()
            settings.lastBookProgress = progress

            rows = text?.replace("([\\n\\r\\.\\?!])".toRegex(), "$1|")?.split("|")?.map { it.trim() }?.filter { it.length > 0 }?.map { TextRow(it) }
            fragment.setTextRows(rows)

        } catch (e: Throwable) {
            e.printStackTrace()
            fragment.showError(e.localizedMessage)
        }


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







