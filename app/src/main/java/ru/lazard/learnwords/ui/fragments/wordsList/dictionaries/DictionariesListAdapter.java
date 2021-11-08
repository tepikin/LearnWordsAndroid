package ru.lazard.learnwords.ui.fragments.wordsList.dictionaries;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.lazard.learnwords.R;
import ru.lazard.learnwords.model.Dictionary;

/**
 * Created by Egor on 03.06.2016.
 */
public class DictionariesListAdapter extends RecyclerView.Adapter<DictionariesItemViewHolder> {

    private List<Dictionary> list = new ArrayList<>();


    @Override
    public int getItemCount() {
        return list.size();
    }

    public List<Dictionary> getItems() {
        return list;
    }

    @Override
    public void onBindViewHolder(DictionariesItemViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public DictionariesItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View  view= LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_dictionaries_list_item, parent, false);
        return new DictionariesItemViewHolder(view,this);
    }

    public void setList(List<Dictionary> list) {
        this.list = list;
    }
}
