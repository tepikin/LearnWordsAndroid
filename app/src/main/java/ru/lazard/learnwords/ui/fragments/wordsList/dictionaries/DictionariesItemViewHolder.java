package ru.lazard.learnwords.ui.fragments.wordsList.dictionaries;

import android.content.DialogInterface;
import android.content.res.Resources;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import ru.lazard.learnwords.R;
import ru.lazard.learnwords.model.Dictionary;
import ru.lazard.learnwords.model.Model;
import ru.lazard.learnwords.model.Word;
import ru.lazard.learnwords.ui.activities.main.MainActivity;
import ru.lazard.learnwords.ui.fragments.wordsList.words.WordsListFragment;

/**
 * Created by Egor on 03.06.2016.
 */
public class DictionariesItemViewHolder extends RecyclerView.ViewHolder {


    private final TextView nameView;
    private final TextView countView;
    private final View baseView;
    private DictionariesListAdapter adapter;


    public DictionariesItemViewHolder(View itemView, DictionariesListAdapter adapter) {
        super(itemView);
        this.adapter=adapter;
        baseView =itemView;
        nameView = (TextView) itemView.findViewById(R.id.name);
        countView = (TextView) itemView.findViewById(R.id.count);
    }

    public void bind(final Dictionary dictionary) {
        setEmptyText(nameView,dictionary.getName());


        int wordsCount = 0;
        int learnWordsCount = 0;
        int readyWordsCount = 0;
        for (Word word : Model.getInstance().getWords()) {
            if (word.getDictionaryId()==dictionary.getId()){
                wordsCount++;
                switch (word.getStatus()){
                    case Word.STATUS_NONE:break;
                    case Word.STATUS_LEARN:
                    case Word.STATUS_CHECK_WRITE:
                    case Word.STATUS_CHECK_TRANSLATE:learnWordsCount++;break;
                    case Word.STATUS_READY:readyWordsCount++;
                }
            }
        }
        String counters = baseView.getContext().getResources().getString(R.string.dictionariesList_countFormat);
        counters = String.format(counters,wordsCount,learnWordsCount,readyWordsCount);
        setEmptyText(countView,counters);

        baseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)v.getContext()).addFragment(WordsListFragment.newInstance(dictionary.getId()),true);
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
                menu.add(resources.getString(R.string.dictionariesList_contextMenu_rename))
                        .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                            showRenameDialog(dictionary);
                        return true;
                    }
                });
                menu.add(resources.getString(R.string.dictionariesList_contextMenu_remove)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        showRemoveDialog(dictionary);
                        return true;
                    }
                });
            }
        });

    }

    private void showRemoveDialog(final Dictionary dictionary) {
        new AlertDialog.Builder(baseView.getContext())
                .setTitle(R.string.dictionariesList_dialog_remove_title)
                .setMessage(R.string.dictionariesList_dialog_remove_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Model.getInstance().removeDictionary(dictionary);
                        adapter.getItems().remove(dictionary);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(android.R.string.cancel,null).show();
    }

    private void showRenameDialog(final Dictionary dictionary) {
        final View view = LayoutInflater.from(baseView.getContext()).inflate(R.layout.fragment_dictionary_edit,null);
        new AlertDialog.Builder(baseView.getContext())
                .setTitle(R.string.dictionariesList_dialog_rename_title)
                .setMessage(R.string.dictionariesList_dialog_rename_message)
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = ((TextView)view.findViewById(R.id.name)).getText().toString();
                        Model.getInstance().renameDictionary(dictionary,text);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(android.R.string.cancel,null).show();
    }


    private void setEmptyText(TextView textView, String text) {
        if (TextUtils.isEmpty(text)) {
            textView.setText("");
        } else {
            textView.setText(text);
        }
    }

}
