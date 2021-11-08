package ru.lazard.learnwords.ui.fragments.wordsList.words;

import android.content.res.Resources;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ru.lazard.learnwords.R;
import ru.lazard.learnwords.model.Model;
import ru.lazard.learnwords.model.Word;
import ru.lazard.learnwords.ui.activities.main.MainActivity;
import ru.lazard.learnwords.ui.fragments.wordsList.edit.WordEditFragment;

/**
 * Created by Egor on 03.06.2016.
 */
public class WordsItemViewHolder extends RecyclerView.ViewHolder {


    private final TextView wordView;
    private final TextView translateView;
    private final TextView transcriptionView;
    private final CheckBox visibleView;
    private final View baseView;
    private WordsListAdapter adapter;


    public WordsItemViewHolder(View itemView,WordsListAdapter adapter) {
        super(itemView);
        this.adapter=adapter;
        baseView =itemView;
        wordView = (TextView) itemView.findViewById(R.id.word);
        translateView = (TextView) itemView.findViewById(R.id.translate);
        transcriptionView = (TextView) itemView.findViewById(R.id.transcription);
        visibleView = (CheckBox) itemView.findViewById(R.id.status);
    }

    public void bind(final Word word) {
        setEmptyText(wordView,word.getWord());
        setEmptyText(translateView,word.getTranslate());
        String transcription = TextUtils.isEmpty(word.getTranscription()) ? "" : ("[" + word.getTranscription() + "]");
        setEmptyText(transcriptionView,transcription);
        String statusText = visibleView.getResources().getStringArray(R.array.wordsList_status_items)[word.getStatus()];
        setEmptyText(visibleView, statusText);

        visibleView.setEnabled(word.getStatus()<=Word.STATUS_LEARN);
        visibleView.setChecked(word.getStatus()>0);
        visibleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int newStatus = Word.STATUS_NONE;
                if (word.getStatus()==Word.STATUS_NONE){
                    newStatus=Word.STATUS_LEARN;
                }
                Model.getInstance().setWordStatus(word,newStatus);
                bind(word);
            }
        });

        baseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)v.getContext()).addFragment(WordEditFragment.newInstance(word,word.getDictionaryId()),true);
            }
        });
        baseView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        baseView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                Resources resources = v.getResources();
                //menu.setHeaderTitle(resources.getString(R.string.wordsList_contextMenu_title));
                menu.add(resources.getString(R.string.wordsList_contextMenu_edit))
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                            ((MainActivity)baseView.getContext()).addFragment(WordEditFragment.newInstance(word,word.getDictionaryId()),true);
                        return true;
                    }
                });
                menu.add(resources.getString(R.string.wordsList_contextMenu_createNew)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        ((MainActivity)baseView.getContext()).addFragment(WordEditFragment.newInstance(null,adapter.getDictionaryId()),true);
                        return true;
                    }
                });
                menu.add(resources.getString(R.string.wordsList_contextMenu_remove)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        Model.getInstance().removeWord(word);

                        adapter.getItems().remove(word);
                        adapter.notifyDataSetChanged();
                        return true;
                    }
                });;
            }
        });

    }


    private void setEmptyText(TextView textView, String text) {
        if (TextUtils.isEmpty(text)) {
            textView.setText("");
        } else {
            textView.setText(text);
        }
    }

}
