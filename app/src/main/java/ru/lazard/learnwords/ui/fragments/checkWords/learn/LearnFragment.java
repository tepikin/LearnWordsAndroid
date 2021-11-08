package ru.lazard.learnwords.ui.fragments.checkWords.learn;

import android.animation.Animator;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import ru.lazard.learnwords.R;
import ru.lazard.learnwords.model.Word;
import ru.lazard.learnwords.ui.activities.main.MainActivity;
import ru.lazard.learnwords.utils.view.PlayPauseDrawable;

/**
 * Created by Egor on 02.06.2016.
 */
public class LearnFragment extends Fragment implements View.OnClickListener {
    private View baseLayout;
    private FloatingActionButton floatingActionButton;
    private GestureDetectorCompat gestureDetectorCompat;
    private Animator pausePlayAnimator;
    private PlayPauseDrawable playPauseDrawable;


    private LearnPresenter presenter;
    private ImageView soundView;
    private CheckBox statusView;
    private TextView transcriptionView;
    private TextView translateView;
    private Word word;
    private TextView wordView;

    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.e("TAG","velocityX"+velocityX);
            float absX = Math.abs(velocityX);
            float absY = Math.abs(velocityY);
            if (absX >1000&&absX > absY&&velocityX < 0) {
                presenter.doStep();
            }
            return false;
        }
    };

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
        if (statusView == v) {
            presenter.onStatusViewClick();
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
            view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_learn, container, false);
            baseLayout = view.findViewById(R.id.base_layout);
            wordView = (TextView) view.findViewById(R.id.word);
            translateView = (TextView) view.findViewById(R.id.translate);
            transcriptionView = (TextView) view.findViewById(R.id.transcription);
            statusView = (CheckBox) view.findViewById(R.id.status);
            soundView = (ImageView) view.findViewById(R.id.sound);

            floatingActionButton = ((MainActivity) getActivity()).getFloatingActionButton();

            floatingActionButton.setOnClickListener(this);
            soundView.setOnClickListener(this);
            statusView.setOnClickListener(this);
            floatingActionButton.setVisibility(View.VISIBLE);
            playPauseDrawable = new PlayPauseDrawable(getContext());
            playPauseDrawable.setPlay();
            floatingActionButton.setImageDrawable(playPauseDrawable);
            setStatePause();
            if (presenter == null) {
                presenter = new LearnPresenter(this);
            }

            registerGestureRecognizer();

        }
        return view;
    }

    private void registerGestureRecognizer() {
        gestureDetectorCompat=new GestureDetectorCompat(getContext(), gestureListener);
        baseLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetectorCompat.onTouchEvent(event);
                return true;
            }
        });
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
        ((MainActivity) getActivity()).getFloatingActionButton().setVisibility(View.INVISIBLE);
        ((MainActivity) getActivity()).getFloatingActionButton().show();
        ((MainActivity) getActivity()).setSelectedNavigationMenu(R.id.nav_learnWords);
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
            statusView.setVisibility(View.GONE);
        }else {
            statusView.setVisibility(View.VISIBLE);
        }

        String transcription = TextUtils.isEmpty(randomWord.getTranscription()) ? "" : ("[" + randomWord.getTranscription() + "]");

        setNotNullText(wordView, randomWord.getWord());
        setNotNullText(transcriptionView, transcription);
        setNotNullText(translateView, randomWord.getTranslate());
        statusView.setChecked(randomWord.getStatus()>Word.STATUS_LEARN);
    }

    private void setNotNullText(TextView textView, String text) {
        if (TextUtils.isEmpty(text)) {
            textView.setText("");
        } else {
            textView.setText(text);
        }
    }
}
