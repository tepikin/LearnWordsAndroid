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
import android.app.SearchManager
import android.content.DialogInterface
import android.graphics.Color
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.support.v7.widget.SearchView
import android.support.v7.widget.SimpleItemAnimator
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Range
import android.widget.Toast
import ru.lazard.learnwords.model.Dictionary
import ru.lazard.learnwords.model.Model
import ru.lazard.learnwords.model.Word
import ru.lazard.learnwords.ui.fragments.preferences.Settings
import ru.lazard.learnwords.ui.fragments.preferences.SettingsFragment
import ru.lazard.learnwords.ui.fragments.wordsList.edit.WordEditFragment


class ReadBookFragment : View.OnClickListener, Fragment() {
    private var baseLayout: View? = null
    private var bookTextRecyclerView: RecyclerView? = null
    private var floatingActionButton: FloatingActionButton? = null
    private var pausePlayAnimator: Animator? = null
    private val playPauseDrawable by lazy { PlayPauseDrawable(context).apply { setPlay() } }
    private val adapter by lazy { BookTextAdapter(context) }
    private val recyclerLayoutManager by lazy { LinearLayoutManager(context) }
    private val presenter by lazy { ReadBookPresenter(this) }
    private val settings by lazy { Settings(context) }

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
            presenter.onFloatingActionButtonClick(recyclerLayoutManager?.findFirstVisibleItemPosition())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_book_read, menu)

        menu?.findItem(R.id.menu_readSrc)?.isChecked =settings.bookReaded_isReadSrc
        menu?.findItem(R.id.menu_readDst)?.isChecked =settings.bookReaded_isReadDst
        menu?.findItem( R.id.menu_readSrcWordByWord)?.isChecked =settings.bookReaded_isReadSrcWordByWord
        menu?.findItem( R.id.menu_readDstWordByWord)?.isChecked =settings.bookReaded_isReadDstWordByWord
        menu?.findItem(R.id.menu_readOnlyWords)?.isChecked =settings.bookReaded_isReadOnlyWords
        menu?.findItem(R.id.menu_useTranslator)?.isChecked =settings.bookReaded_isUseTranslator
        menu?.findItem(R.id.menu_readAloud)?.isChecked =settings.bookReaded_isReadAloud


        val searchManager = context.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu?.findItem(R.id.menu_search)?.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.componentName))
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                presenter.onSearch(query)
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                presenter.onSearch(query)
                return true
            }
        })

        if (presenter != null) {
            if (!TextUtils.isEmpty(presenter.searchQuery)) {
                searchView.setQuery(presenter.searchQuery, true)
                searchView.setIconified(false)
            }
        }

        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.menu_openBook) {
            showFileChoicer()
            return true
        }
        if (id == R.id.menu_viewWords) {
            //TODO
            return true
        }

        if (id == R.id.menu_useTranslator) {
            item.isChecked=!item.isChecked
            settings.bookReaded_isUseTranslator= item.isChecked
            presenter?.onReadOrderChanged()
            return false
        }
        if (id == R.id.menu_readSrc) {
            item.isChecked=!item.isChecked
            settings.bookReaded_isReadSrc = item.isChecked
            presenter?.onReadOrderChanged()
            return false
        }
        if (id == R.id.menu_readDst) {
            item.isChecked=!item.isChecked
            settings.bookReaded_isReadDst = item.isChecked
            presenter?.onReadOrderChanged()
            return false
        }
        if (id == R.id.menu_readSrcWordByWord) {
            item.isChecked=!item.isChecked
            settings.bookReaded_isReadSrcWordByWord = item.isChecked
            presenter?.onReadOrderChanged()
            return false
        }
        if (id == R.id.menu_readDstWordByWord) {
            item.isChecked=!item.isChecked
            settings.bookReaded_isReadDstWordByWord = item.isChecked
            presenter?.onReadOrderChanged()
            return false
        }
        if (id == R.id.menu_readOnlyWords) {
            item.isChecked=!item.isChecked
            settings.bookReaded_isReadOnlyWords = item.isChecked
            presenter?.onReadOrderChanged()
            return false
        }
        if (id == R.id.menu_readAloud) {
            item.isChecked=!item.isChecked
            settings.bookReaded_isReadAloud = item.isChecked
            presenter?.onReadOrderChanged()
            return false
        }
        return super.onOptionsItemSelected(item)
    }

var viewCache :View? =null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = super.onCreateView(inflater, container, savedInstanceState)
        if (view ==null){view = viewCache }
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
            val bookProgress :Float? = if (arguments.containsKey(KEY_BOOK_PROGRESS) )arguments?.getFloat(KEY_BOOK_PROGRESS) else null
            presenter.openStartBook(bookUri, bookProgress)
        }
        this.viewCache = view
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









    inner class BookTextAdapter(val context: Context) : RecyclerView.Adapter<TextRowViewHolder>() {
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


    inner class TextRowViewHolder(val parent: ViewGroup?) : RecyclerView.ViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.fragment_book_read_text_row, parent, false)) {
        val textView by lazy { itemView.findViewById<TextView>(R.id.text) }
        val settings:Settings by lazy { Settings(itemView.context) }
        fun bind(textRow: TextRow) {
            val text = textRow.run {
                var result:String =""
                if (settings.bookReaded_isReadSrc&&src!=null){result+="\n"+src}
                if (settings.bookReaded_isReadSrcWordByWord&&srcWithNewWords!=null){result+="\n"+srcWithNewWords}
                if (settings.bookReaded_isReadDst&&dst!=null){result+="\n"+dst}
                if (settings.bookReaded_isReadDstWordByWord&&dstWithNewWords!=null){result+="\n"+dstWithNewWords}
                if (settings.bookReaded_isReadOnlyWords&&wordsTranslated.size>0){result+="\n"+wordsTranslated?.map { "" + it.word + " -> " + it.translate }?.joinToString("\n")}
                if (result.isEmpty())result+="\n"+src
                result=result.trim()
                result
            }


            fun SpannableString.addSpanClickable(start:Int,end:Int,color:Int?,action:()->Unit){
                this.getSpans(start,end,ClickableSpan::class.java).forEach {this.removeSpan(it)}
                this.setSpan(object:ClickableSpan(){
                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        color?.let { ds.color=it }
                        ds.isUnderlineText=false
                    }
                    override fun onClick(p0: View) = action()
                },start,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            val spannable = SpannableString(text)



            val textColor = when (textRow.state) {
                TextRow.State.none -> Color.DKGRAY
                TextRow.State.reading -> Color.BLACK
                TextRow.State.readed -> Color.LTGRAY
            }

            text.split("[^\\w]".toRegex()).filter { it.length>0 }.map { it.toLowerCase() }.distinct().flatMap { "(^|[^\\w])($it)[^\\w]".toRegex(RegexOption.IGNORE_CASE).findAll("$text").toList() }
                    .map{
                        it.groups.get(2)
                    }?.filterNotNull().forEach {
group->
                spannable.addSpanClickable(group.range.start,group.range.endInclusive,textColor){

                    val word = Model.getInstance().getWordsByDictionary(6)?.find { it?.word?.toLowerCase()==group?.value?.toLowerCase() ||it?.translate?.toLowerCase()==group?.value?.toLowerCase() }
                    word?.let {
                        presenter.pause()
                        (activity as? MainActivity)?.addFragment(WordEditFragment.newInstance(word ,6),true)
                    }

                }
            }


            textRow?.wordsTranslated?.flatMap { listOf(it to "(${it.word} : ${it.translate})",it to "(${it.translate} : ${it.word})") }?.forEach {
                val word = it.second
                var startIndex =0
                while (text.indexOf(word,startIndex)>=0) {
                    startIndex = text.indexOf(word,startIndex)
                    spannable.addSpanClickable(startIndex,startIndex + word.length, null){
                        if (it.first.status<=Word.STATUS_LEARN){
                            Model.getInstance().setWordStatus(it.first,Word.STATUS_CHECK_TRANSLATE)
                            Toast.makeText(itemView.context,"${it.first.word} set to CHECK",Toast.LENGTH_SHORT).show()
                            presenter.onReadOrderChanged()
                        }else{
                            Model.getInstance().setWordStatus(it.first,Word.STATUS_LEARN)
                            Toast.makeText(itemView.context,"${it.first.word} set to LEARN",Toast.LENGTH_SHORT).show()
                            presenter.onReadOrderChanged()
                        }
                    }
                    startIndex++;
                }
            }

            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.text =spannable




            textView.setTextColor(textColor)
        }

    }
}




fun Fragment.runOnUiThread(function:()->Unit){
    activity?.runOnUiThread(function)
}

fun String.findAll(substring:String,ignoreCase:Boolean = true):List<IntRange>{
    val result = mutableListOf<IntRange>()
    var startIndex = 0;
    while(startIndex >=0) {
        startIndex  = this.indexOf(substring, startIndex, ignoreCase)
        if (startIndex >=0){
            result.add(IntRange(startIndex,startIndex+substring.length))
        }
    }
    return result
}



