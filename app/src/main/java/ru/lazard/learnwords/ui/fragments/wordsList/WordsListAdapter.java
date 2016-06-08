package ru.lazard.learnwords.ui.fragments.wordsList;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.lazard.learnwords.R;
import ru.lazard.learnwords.model.Word;

/**
 * Created by Egor on 03.06.2016.
 */
public class WordsListAdapter extends RecyclerView.Adapter<WordsItemViewHolder> {

    private List<Word> list = new ArrayList<>();


    @Override
    public int getItemCount() {
        return list.size();
    }

    public List<Word> getItems() {
        return list;
    }

    @Override
    public void onBindViewHolder(WordsItemViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public WordsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View  view= LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_words_list_item, parent, false);
        return new WordsItemViewHolder(view,this);
    }

    public void setList(List<Word> list) {
        this.list = list;
    }
}
