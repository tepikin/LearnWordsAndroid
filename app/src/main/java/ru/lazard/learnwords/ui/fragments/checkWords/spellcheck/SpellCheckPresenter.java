package ru.lazard.learnwords.ui.fragments.checkWords.spellcheck;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import java.util.List;
import java.util.Locale;

import ru.lazard.learnwords.model.Model;
import ru.lazard.learnwords.model.Word;
import ru.lazard.learnwords.speach.TTS;
import ru.lazard.learnwords.ui.activities.main.MainActivity;
import ru.lazard.learnwords.ui.fragments.checkWords.spellcheck.SpellCheckFragment.State;
import ru.lazard.learnwords.ui.fragments.preferences.Settings;


public class SpellCheckPresenter implements FragmentManager.OnBackStackChangedListener {
    private final SpellCheckFragment fragment;
    private final Context context;
    private Word randomWord;
    private Settings settings;
    private State state = State.start;


    public SpellCheckPresenter(SpellCheckFragment mainActivity) {
        this.fragment = mainActivity;
        context = fragment.getContext();
        settings = new Settings(context);
        fragment.getActivity().getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    public void doStep() {
        state = State.start;
        randomWord = Model.getInstance().getRandomWordWithStatusLoverOrEqualThen(Word.STATUS_CHECK_WRITE);
        fragment.showWord(randomWord, state);
        if (settings.isReadWords()&&settings.isReadTranslate() && randomWord != null) {
            getTts().speak(randomWord.getTranslate(), settings.getSpeedReadTranslate(), Locale.getDefault(), null);
        }
    }


    @Override
    public void onBackStackChanged() {
        if (fragment == null) return;
        List<Fragment> fragments = fragment.getActivity().getSupportFragmentManager().getFragments();
        if (fragments != null && fragments.size() > 0) {
            Fragment topFragment = fragments.get(fragments.size() - 1);
            if (topFragment != fragment) {
                getTts().stop();
            }
        }
    }

    public void onDestroy() {
        getTts().stop();
        fragment.getActivity().getSupportFragmentManager().removeOnBackStackChangedListener(this);
    }

    public void onNextViewClick() {
        if (randomWord == null) return;
        if (state == State.start) {
            doStep();
        } else if (state == State.fail) {
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
        getTts().stop();
    }

    public void onUnknownViewClick() {
        if (randomWord == null) return;
        if (state == State.start) {
            state = State.fail;
            fragment.showWord(randomWord, state);
            if (settings.isReadWords() ) {
                getTts().speak(randomWord.getWord(), settings.getSpeedReadWords(), Locale.ENGLISH, null);
            }
        } else if (state == State.fail) {
            doStep();
        }
    }

    public void onApply(String word) {
        if (randomWord == null) return;
        if (state == State.start) {
            if (!TextUtils.isEmpty(word)&&word.equalsIgnoreCase(randomWord.getWord())) {
                Model.getInstance().setWordStatus(randomWord,Word.STATUS_READY);
                doStep();
                return;
            } else {
                state = State.fail;
                fragment.showWord(randomWord, state);
                if (settings.isReadWords() ) {
                    getTts().speak(randomWord.getWord(), settings.getSpeedReadWords(), Locale.ENGLISH, null);
                }
            }
        } else if (state == State.fail) {
            doStep();
        }
    }

    public void restoreState() {
        fragment.setSoundEnable(settings.isReadWords());
        if (randomWord == null) {
            doStep();
            return;
        }
        fragment.showWord(randomWord, state);
    }

    private TTS getTts() {
        return ((MainActivity) context).getTts();
    }
}
