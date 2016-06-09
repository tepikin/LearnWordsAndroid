package ru.lazard.learnwords.ui.fragments.check;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.lazard.learnwords.R;
import ru.lazard.learnwords.model.Word;
import ru.lazard.learnwords.ui.activities.main.MainActivity;

/**
 * Created by Egor on 02.06.2016.
 */
public class CheckWordsFragment extends Fragment implements View.OnClickListener {
    private View applyView;
    private View baseLayout;
    private View cancelView;
    private View nextCancelView;
    private View nextApplyView;


    private CheckWordsPresenter presenter;
    private ImageView soundView;
    private TextView transcriptionView;
    private ImageView translateIcon;
    private View translateLayout;
    private TextView translateView;
    private Word word;
    private TextView wordView;

    @Override
    public void onClick(View v) {
        if (soundView == v) {
            presenter.onSoundViewClick();
        }
        if (cancelView == v) {
            presenter.onCancelViewClick();
        }
        if (nextCancelView == v) {
            presenter.onNextViewClick();
        }
        if (nextApplyView == v) {
            presenter.onNextViewClick();
        }
        if (applyView == v) {
            presenter.onApplyViewClick();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main2, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_check, container, false);
            baseLayout = view.findViewById(R.id.base_layout);
            wordView = (TextView) view.findViewById(R.id.word);
            transcriptionView = (TextView) view.findViewById(R.id.transcription);
            translateView= (TextView) view.findViewById(R.id.translate);
            translateLayout = (View) view.findViewById(R.id.translateLayout);
            translateIcon = (ImageView) view.findViewById(R.id.translateIcon);
            soundView = (ImageView) view.findViewById(R.id.sound);
            cancelView = (View) view.findViewById(R.id.cancel);
            nextCancelView = (View) view.findViewById(R.id.nextCancel);
            nextApplyView = (View) view.findViewById(R.id.nextApply);
            applyView = (View) view.findViewById(R.id.apply);

            soundView.setOnClickListener(this);
            cancelView.setOnClickListener(this);
            nextCancelView.setOnClickListener(this);
            nextApplyView.setOnClickListener(this);
            applyView.setOnClickListener(this);
            if (presenter == null) {
                presenter = new CheckWordsPresenter(this);
            }
        }
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getFloatingActionButton().hide();
        ((MainActivity) getActivity()).setSelectedNavigationMenu(R.id.nav_checkWords);
        presenter.onResume();
    }

    public void setSoundEnable(boolean isReadWords) {
        soundView.setImageResource(isReadWords ? R.drawable.ic_volume_up_grey600_24dp : R.drawable.ic_volume_off_grey600_24dp);
    }

public enum State{start,success,fail};

    public void showWord(Word randomWord,State state) {
        if (randomWord == null) {
            randomWord = new Word("No words selected", "Выбирите слова для изучения");
        }



        String transcription = TextUtils.isEmpty(randomWord.getTranscription()) ? "" : ("[" + randomWord.getTranscription() + "]");
        setNotNullText(wordView, randomWord.getWord());
        setNotNullText(transcriptionView, transcription);
        setNotNullText(translateView, randomWord.getTranslate());

        cancelView.setVisibility(View.GONE);
        nextCancelView.setVisibility(View.GONE);
        nextApplyView.setVisibility(View.GONE);
        applyView.setVisibility(View.GONE);
        translateLayout.setVisibility(View.INVISIBLE);

        if (State.start==state){
            cancelView.setVisibility(View.VISIBLE);
            applyView.setVisibility(View.VISIBLE);
        }
        if (State.success==state){
            cancelView.setVisibility(View.VISIBLE);
            applyView.setVisibility(View.GONE);
            nextApplyView.setVisibility(View.VISIBLE);
            translateLayout.setVisibility(View.VISIBLE);
            translateIcon.setImageResource(R.drawable.ic_highlight_off_black_362dp);
            translateIcon.setColorFilter(Color.GREEN);
        }
        if (State.fail==state){
            applyView.setVisibility(View.VISIBLE);
            nextCancelView.setVisibility(View.VISIBLE);
            translateLayout.setVisibility(View.VISIBLE);
            translateIcon.setImageResource(R.drawable.ic_highlight_off_black_362dp);
            translateIcon.setColorFilter(Color.RED);
        }
    }

    private void setNotNullText(TextView textView, String text) {
        if (TextUtils.isEmpty(text)) {
            textView.setText("");
        } else {
            textView.setText(text);
        }
    }
}