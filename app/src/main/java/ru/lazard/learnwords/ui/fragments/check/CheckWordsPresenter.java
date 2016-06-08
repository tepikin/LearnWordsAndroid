package ru.lazard.learnwords.ui.fragments.check;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.lazard.learnwords.model.Model;
import ru.lazard.learnwords.model.Word;
import ru.lazard.learnwords.speach.TTS;
import ru.lazard.learnwords.ui.activities.main.MainActivity;
import ru.lazard.learnwords.ui.fragments.preferences.Settings;


public class CheckWordsPresenter implements FragmentManager.OnBackStackChangedListener {
    public static final String KEY_WORD_ID = "Word_ID";
    private final CheckWordsFragment fragment;
    private final Context context;
    private AtomicBoolean cancelSync = new AtomicBoolean(false);
    private Handler handler;
    private boolean isPlay;
    private Word randomWord;
    private Settings settings;
    private Runnable playProcess = new Runnable() {
        @Override
        public void run() {

            doStep();
        }
    };

    public CheckWordsPresenter(CheckWordsFragment mainActivity) {
        this.fragment = mainActivity;
        context = fragment.getContext();
        settings = new Settings(context);
        handler = new Handler(Looper.getMainLooper());
        fragment.getActivity().getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    public void doStep() {

        randomWord = Model.getInstance().getRandomWordForLearning();

        startWord(randomWord);
    }

    @Override
    public void onBackStackChanged() {
        List<Fragment> fragments = fragment.getActivity().getSupportFragmentManager().getFragments();
        if (fragments!=null&&fragments.size()>0){
            Fragment topFragment = fragments.get(fragments.size() - 1);
            if (topFragment!=fragment&&isPlay){
                pause();
            }
        }
    }

    public void onDestroy() {
        pause();
        fragment.getActivity().getSupportFragmentManager().removeOnBackStackChangedListener(this);
    }

    public void onDetach() {
        pause();
    }

    public void onFloatingActionButtonClick() {
        if (isPlay) {
            pause();
            return;
        }
        if (settings.isAutoWordsSwitch()) {
            play();
        } else {
            doStep();
        }
    }



    public void onResume() {
        restoreState();
    }

    public void onSoundViewClick() {
        boolean isReadWords = !settings.isReadWords();
        settings.setReadWords(isReadWords);
        fragment.setSoundEnable(isReadWords);
        cancelSync.set(true);
        cancelSync = new AtomicBoolean(false);
        getTts().stop();

    }

    public void onStatusViewClick() {
        if (randomWord != null) {
            if (randomWord.getStatus() == Word.STATUS_NONE || randomWord.getStatus() == Word.STATUS_LEARN) {
                randomWord.setStatus(Word.STATUS_CHECK_1);
            } else {
                randomWord.setStatus(Word.STATUS_LEARN);
            }
        }
    }

    public void play() {
        pause();
        this.isPlay = true;
        startWord(randomWord);//doStep();

        fragment.setStatePlay();
    }

    public void restoreState() {
        if (randomWord == null) {
            randomWord = Model.getInstance().getRandomWordForLearning();
        }
        fragment.showWord(randomWord);
        if (isPlay){
            fragment.setStatePlay();
        }else{
            fragment.setStatePause();
        }
        fragment.setSoundEnable(settings.isReadWords());

    }


    private void onStepDone() {
        if (isPlay) {
            int delayMillis = settings.delayBetweenWords() * 1000 + 500;
            handler.postDelayed(playProcess, delayMillis);
        }
    }

    public void pause() {
        this.isPlay = false;
        cancelSync.set(true);
        cancelSync = new AtomicBoolean(false);
        handler.removeCallbacks(playProcess);
        getTts().stop();
        fragment.setStatePause();
    }


    private void playWord(final Word randomWord, final Runnable callback) {
        if (randomWord == null) {
            if (callback != null) callback.run();
            return;
        }
        final AtomicBoolean cancel = cancelSync;
        getTts().speak(randomWord.getWord(), settings.speedReadWords(), Locale.ENGLISH, new Runnable() {
            @Override
            public void run() {
                if (cancel.get()) {
                    if (callback != null) callback.run();
                    return;
                }
                if (!settings.isReadTranslate()) {
                    if (callback != null) callback.run();
                    return;
                }
                getTts().speak(randomWord.getTranslate(), settings.speedReadTranslate(), null, callback);
            }
        });
    }

    private TTS getTts() {
        return ((MainActivity) context).getTts();
    }

    private void startWord(Word randomWord) {
        fragment.showWord(randomWord);
        if (randomWord == null) {
            return;
        }
        if (settings.isBlinkEnable()) {
            fragment.blink();
        }
        if (settings.isReadWords()) {
            playWord(randomWord, new Runnable() {
                @Override
                public void run() {
                    onStepDone();
                }
            });
        } else {
            onStepDone();
        }
    }
}
