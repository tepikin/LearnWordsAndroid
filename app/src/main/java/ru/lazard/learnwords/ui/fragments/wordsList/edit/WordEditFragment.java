package ru.lazard.learnwords.ui.fragments.wordsList.edit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.lazard.learnwords.R;
import ru.lazard.learnwords.db.DAO;
import ru.lazard.learnwords.model.Model;
import ru.lazard.learnwords.model.Word;
import ru.lazard.learnwords.ui.activities.main.MainActivity;

/**
 * Created by Egor on 03.06.2016.
 */
public class WordEditFragment extends Fragment {


    public static final String KEY_WORD_ID = "KEY_WORD_ID";
    private AppCompatSpinner spinerView;
    private AppCompatEditText transcriptionView;
    private AppCompatEditText translateView;
    private Word word;
    private AppCompatEditText wordView;

    public static Fragment newInstance(Word word) {
        WordEditFragment wordEditFragment = new WordEditFragment();
        if (word!=null) {
            Bundle args = new Bundle();
            args.putInt(KEY_WORD_ID, word.getId());
            wordEditFragment.setArguments(args);
        }
        return wordEditFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_word_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_word_edit, container, false);
            wordView =(AppCompatEditText) view.findViewById(R.id.word);
            transcriptionView =(AppCompatEditText) view.findViewById(R.id.transcription);
            translateView =(AppCompatEditText) view.findViewById(R.id.translate);
            spinerView =(AppCompatSpinner) view.findViewById(R.id.status);

            if (getArguments()!=null){
                int wordId=getArguments().getInt(KEY_WORD_ID,-1);
                Word word = Model.getInstance().getWordById(wordId);
                setWord(word);
            }

        }
        return view;
    }

    private void setWord(Word word) {
        this.word=word;
        if (word==null)return;
        setNotEmptyString(wordView,word.getWord());
        setNotEmptyString(translateView,word.getTranslate());
        setNotEmptyString(transcriptionView,word.getTranscription());
        spinerView.setSelection(word.getStatus());
    }
    private void setNotEmptyString(TextView view,String text){
        if (view==null)return;
        if (TextUtils.isEmpty(text)){
            view.setText("");
        }else{
            view.setText(text);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getFloatingActionButton().hide();
        //((MainActivity) getActivity()).setSelectedNavigationMenu(R.id.nav_wordsList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_done) {
onApplay();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onApplay() {

        if (!validate())return;

        if (word==null) {
            word = new Word();
            bindToWord(word);
            Model.getInstance().getWords().add(word);
            DAO.insertWord(word);
            getActivity().onBackPressed();
        }else{
            bindToWord(word);
            DAO.updateWord(word);
            getActivity().onBackPressed();
        }

    }

    private void bindToWord(Word word) {
        word.setWord(wordView.getText().toString());
        word.setTranslate(translateView.getText().toString());
        word.setTranscription(transcriptionView.getText().toString());
        word.setStatus(spinerView.getSelectedItemPosition());
    }

    private boolean validate() {
        if (TextUtils.isEmpty(wordView.getText())){
            wordView.setError(wordView.getResources().getString(R.string.wordEdit_word_error));
            return false;
        }
        return true;
    }
}