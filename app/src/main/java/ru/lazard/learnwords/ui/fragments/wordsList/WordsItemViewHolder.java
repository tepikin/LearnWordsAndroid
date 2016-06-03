package ru.lazard.learnwords.ui.fragments.wordsList;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import ru.lazard.learnwords.R;
import ru.lazard.learnwords.model.Word;

/**
 * Created by Egor on 03.06.2016.
 */
public class WordsItemViewHolder extends RecyclerView.ViewHolder {


    private final TextView wordView;
    private final TextView translateView;
    private final CheckBox visibleView;


    public WordsItemViewHolder(View itemView) {
        super(itemView);
        wordView = (TextView) itemView.findViewById(R.id.word);
        translateView = (TextView) itemView.findViewById(R.id.translate);
        visibleView = (CheckBox) itemView.findViewById(R.id.switch1);
    }

    public void bind(Word word) {
        wordView.setText(word.getWord());
        translateView.setText(word.getTranslate());
        visibleView.setChecked(word.isVisible());
    }
}
