package ru.lazard.learnwords.ui.fragments.check;

import android.animation.Animator;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import ru.lazard.learnwords.utils.view.PlayPauseDrawable;

/**
 * Created by Egor on 02.06.2016.
 */
public class CheckWordsFragment extends Fragment implements View.OnClickListener {
    private View baseLayout;
    private FloatingActionButton floatingActionButton;
    private Animator pausePlayAnimator;
    private PlayPauseDrawable playPauseDrawable;


    private CheckWordsPresenter presenter;
    private ImageView soundView;
    private TextView transcriptionView;
    private TextView translateView;
    private Word word;
    private TextView wordView;

    public void blink() {
        baseLayout.setSelected(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                baseLayout.setSelected(false);
            }
        }, 100l);
    }

    @Override
    public void onClick(View v) {
        if (floatingActionButton == v) {
            presenter.onFloatingActionButtonClick();
        }
        if (soundView == v) {
            presenter.onSoundViewClick();
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
            translateView = (TextView) view.findViewById(R.id.translate);
            transcriptionView = (TextView) view.findViewById(R.id.transcription);
            soundView = (ImageView) view.findViewById(R.id.sound);

            floatingActionButton = ((MainActivity) getActivity()).getFloatingActionButton();

            floatingActionButton.setOnClickListener(this);
            soundView.setOnClickListener(this);
            floatingActionButton.setVisibility(View.VISIBLE);
            playPauseDrawable = new PlayPauseDrawable(getContext());
            playPauseDrawable.setPlay();
            floatingActionButton.setImageDrawable(playPauseDrawable);
            setStatePause();
            if (presenter == null) {
                presenter = new CheckWordsPresenter(this);
            } else {
                presenter.restoreState();
            }
        }
        return view;
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        presenter.onDetach();
    }



    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getFloatingActionButton().hide();
        ((MainActivity) getActivity()).setSelectedNavigationMenu(R.id.nav_checkWords);
        presenter.onResume();
    }

    public void setSoundEnable(boolean isReadWords) {
        soundView.setImageResource(isReadWords?R.drawable.ic_volume_up_grey600_24dp:R.drawable.ic_volume_off_grey600_24dp);
    }


    public void setStatePause() {
        //floatingActionButton.setImageResource(android.R.drawable.ic_media_play);
        if (pausePlayAnimator !=null){pausePlayAnimator.cancel();}
        pausePlayAnimator = playPauseDrawable.getAnimatorToPlay();
        pausePlayAnimator.start();
    }

    public void setStatePlay() {
        if (pausePlayAnimator !=null){pausePlayAnimator.cancel();}
        pausePlayAnimator = playPauseDrawable.getAnimatorToPause();
        pausePlayAnimator.start();
    }

    public void showWord(Word randomWord) {
        if (randomWord == null) {
            randomWord = new Word("No words selected", "Выбирите слова для изучения");
        }

        String transcription = TextUtils.isEmpty(randomWord.getTranscription()) ? "" : ("[" + randomWord.getTranscription() + "]");

        setNotNullText(wordView, randomWord.getWord());
        setNotNullText(transcriptionView, transcription);
        setNotNullText(translateView, randomWord.getTranslate());
    }

    private void setNotNullText(TextView textView, String text) {
        if (TextUtils.isEmpty(text)) {
            textView.setText("");
        } else {
            textView.setText(text);
        }
    }
}
