package ru.lazard.learnwords.ui.fragments.wordsList;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import ru.lazard.learnwords.R;
import ru.lazard.learnwords.db.DAO;
import ru.lazard.learnwords.model.Word;

/**
 * Created by Egor on 03.06.2016.
 */
public class WordsItemViewHolder extends RecyclerView.ViewHolder {


    private final TextView wordView;
    private final TextView translateView;
    private final TextView transcriptionView;
    private final CheckBox visibleView;
    private final TextView statusView;


    public WordsItemViewHolder(View itemView) {
        super(itemView);
        wordView = (TextView) itemView.findViewById(R.id.word);
        translateView = (TextView) itemView.findViewById(R.id.translate);
        transcriptionView = (TextView) itemView.findViewById(R.id.transcription);
        statusView = (TextView) itemView.findViewById(R.id.status);
        visibleView = (CheckBox) itemView.findViewById(R.id.switch1);
    }

    public void bind(final Word word) {
        setEmptyText(wordView,word.getWord());
        setEmptyText(translateView,word.getTranslate());
        String transcription = TextUtils.isEmpty(word.getTranscription()) ? "" : ("[" + word.getTranscription() + "]");
        setEmptyText(transcriptionView,transcription);
        String statusText = statusView.getResources().getStringArray(R.array.wordsList_status_items)[word.getStatus()];
        setEmptyText(statusView, statusText);

        visibleView.setChecked(word.getStatus()>0);
        visibleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int newStatus = Word.STATUS_NONE;
                if (word.getStatus()==Word.STATUS_NONE){
                    newStatus=Word.STATUS_LEARN;
                }
                DAO.setStatusForWord(word.getId(), newStatus);
                word.setStatus(newStatus);
                bind(word);
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
