package ru.lazard.learnwords.ui.fragments.books.readBook

import android.animation.Animator
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import ru.lazard.learnwords.R
import ru.lazard.learnwords.ui.activities.main.MainActivity
import ru.lazard.learnwords.utils.view.PlayPauseDrawable
import android.content.Context
import android.widget.TextView
import android.content.Intent
import android.app.Activity
import android.graphics.Color
import android.net.Uri
import android.support.v7.widget.SimpleItemAnimator
import android.widget.Toast


class ReadBookFragment : View.OnClickListener, Fragment() {
    private var baseLayout: View? = null
    private var bookTextRecyclerView: RecyclerView? = null
    private var floatingActionButton: FloatingActionButton? = null
    private var pausePlayAnimator: Animator? = null
    private val playPauseDrawable by lazy { PlayPauseDrawable(context).apply { setPlay() } }
    private val adapter by lazy { BookTextAdapter(context) }
    private val recyclerLayoutManager by lazy { LinearLayoutManager(context) }
    private val presenter by lazy { ReadBookPresenter(this) }

    companion object{
        private val KEY_BOOK_PATH = "KEY_BOOK_PATH"
        private val KEY_BOOK_PROGRESS = "KEY_BOOK_PROGRESS"
        private val REQUEST_CODE_OPEN_FILE = 1

        fun newInstance(bookUri: Uri? = null, progress: Float? = null): Fragment {
            val fragment = ReadBookFragment()
            fragment.arguments = Bundle().apply {
                bookUri?.let { putString(KEY_BOOK_PATH, it.toString()) }
                progress?.let { putFloat(KEY_BOOK_PROGRESS, it) }
            }
            return fragment
        }
    }


    override fun onClick(v: View) {
        if (floatingActionButton === v) {
            presenter.onFloatingActionButtonClick()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.main2, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = super.onCreateView(inflater, container, savedInstanceState)

        if (view == null) {
            view = LayoutInflater.from(context).inflate(ru.lazard.learnwords.R.layout.fragment_book_read, container, false)
            baseLayout = view?.findViewById(ru.lazard.learnwords.R.id.base_layout)

            bookTextRecyclerView = view?.findViewById(ru.lazard.learnwords.R.id.bookText)
            bookTextRecyclerView?.layoutManager = recyclerLayoutManager
            bookTextRecyclerView?.adapter = adapter
            (bookTextRecyclerView?.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations=false

            floatingActionButton = (activity as MainActivity).floatingActionButton
            floatingActionButton?.setOnClickListener(this)
            floatingActionButton?.visibility = View.VISIBLE
            floatingActionButton?.setImageDrawable(playPauseDrawable)
            setStatePause()

            val bookUri = arguments?.getString(KEY_BOOK_PATH)?.let { Uri.parse(it) }
            val bookProgress = arguments?.getFloat(KEY_BOOK_PROGRESS)
            presenter.openStartBook(bookUri, bookProgress)
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == REQUEST_CODE_OPEN_FILE && resultCode == Activity.RESULT_OK) {
            presenter.openChosenBook(resultData)
        }
    }

    fun showFileChoicer() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
//        val mimetypes = arrayOf("application/zip", "application/x-fictionbook","application/fb2")
//        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
        startActivityForResult(intent, REQUEST_CODE_OPEN_FILE)
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
        presenter.onDetach()
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.floatingActionButton?.visibility = View.INVISIBLE
        (activity as? MainActivity)?.floatingActionButton?.show()
        (activity as? MainActivity)?.setSelectedNavigationMenu(ru.lazard.learnwords.R.id.nav_read_book)
        presenter.onResume()
    }

    fun setStatePause() {
        runOnUiThread {
            //floatingActionButton.setImageResource(android.R.drawable.ic_media_play);
            if (pausePlayAnimator != null) {
                pausePlayAnimator?.cancel()
            }
            pausePlayAnimator = playPauseDrawable?.animatorToPlay
            pausePlayAnimator?.start()
        }
    }

    fun setStatePlay() {
        runOnUiThread {
            if (pausePlayAnimator != null) {
                pausePlayAnimator?.cancel()
            }
            pausePlayAnimator = playPauseDrawable?.animatorToPause
            pausePlayAnimator?.start()
        }
    }

    fun showError(localizedMessage: String?) {
        runOnUiThread {
            Toast.makeText(context, localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    fun setTextRows(newRows: List<TextRow>?) {
        newRows?:return
        runOnUiThread {
            adapter.setTextRows(newRows)
        }
    }

    fun updateRow(row: TextRow) {
        runOnUiThread {
            adapter.updateRow(row)
        }
    }

    fun scrollToRow(row: TextRow) {
        runOnUiThread {
            recyclerLayoutManager.scrollToPositionWithOffset(adapter.indexOfRow(row),0)
        }
    }

}


class BookTextAdapter(val context: Context) : RecyclerView.Adapter<TextRowViewHolder>() {
    private val rows = mutableListOf<TextRow>();
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = TextRowViewHolder(parent)
    override fun getItemCount() = rows.size
    override fun onBindViewHolder(holder: TextRowViewHolder, position: Int) = holder.bind(rows[position])
    fun setTextRows(newRows: List<TextRow>) {
        rows.clear()
        rows.addAll(newRows)
        notifyDataSetChanged()
    }

    fun updateRow(row: TextRow) {
        notifyItemChanged(indexOfRow(row))
    }
    fun indexOfRow(row: TextRow) = rows.indexOf(row)

}

fun Fragment.runOnUiThread(function:()->Unit){
    activity?.runOnUiThread(function)
}

class TextRowViewHolder(val parent: ViewGroup?) : RecyclerView.ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.fragment_book_read_text_row, parent, false)) {
    val textView by lazy { itemView.findViewById<TextView>(R.id.text) }
    fun bind(textRow: TextRow) {
        textView.text = textRow.toString()
        textView.setTextColor(when(textRow.state){
            TextRow.State.none -> Color.GRAY
            TextRow.State.reading -> Color.BLACK
            TextRow.State.readed -> Color.LTGRAY
        })
    }

}

