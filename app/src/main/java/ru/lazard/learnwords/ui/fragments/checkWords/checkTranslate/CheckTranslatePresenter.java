package ru.lazard.learnwords.ui.fragments.checkWords.checkTranslate;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import ru.lazard.learnwords.model.Model;
import ru.lazard.learnwords.model.Word;
import ru.lazard.learnwords.speach.TTS;
import ru.lazard.learnwords.ui.activities.main.MainActivity;
import ru.lazard.learnwords.ui.fragments.checkWords.checkTranslate.CheckTranslateFragment.State;
import ru.lazard.learnwords.ui.fragments.preferences.Settings;
import ru.lazard.learnwords.utils.Utils;


public class CheckTranslatePresenter implements FragmentManager.OnBackStackChangedListener {
    private final CheckTranslateFragment fragment;
    private final Context context;
    private int choicedVariant = -1;
    private Word randomWord;
    private int rightVariant = -1;
    private Settings settings;
    private State state = State.start;
    private List<Word> variants = new ArrayList<>();

    public CheckTranslatePresenter(CheckTranslateFragment mainActivity) {
        this.fragment = mainActivity;
        context = fragment.getContext();
        settings = new Settings(context);
        fragment.getActivity().getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    public void doStep() {
        state = State.start;
        randomWord = Model.getInstance().getRandomWordWithStatusLoverOrEqualThen(Word.STATUS_CHECK_TRANSLATE);
        int variantsLength = fragment.getVariantViews().length;
        variants = Model.getRandomSubItems(variantsLength, Model.getInstance().getWords());
        rightVariant= Utils.randomInt(variants.size());
        variants.set(rightVariant,randomWord);
        choicedVariant=-1;
        fragment.showWord(randomWord, state, variants, rightVariant, choicedVariant);
        if (settings.isReadWords() && randomWord != null) {
            getTts().speak(randomWord.getWord(), settings.getSpeedReadWords(), Locale.ENGLISH, null);
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
            fragment.showWord(randomWord, state, variants, rightVariant, choicedVariant);
            if (settings.isReadWords() && settings.isReadTranslate()) {
                getTts().speak(randomWord.getTranslate(), settings.getSpeedReadTranslate(), Locale.getDefault(), null);
            }
        } else if (state == State.fail) {
            doStep();
        }
    }

    public void onVariantViewClick(int choice) {
        if (randomWord == null) return;
        if (state == State.start) {
            choicedVariant = choice;
            if (choicedVariant == rightVariant) {
                Model.getInstance().setWordStatus(randomWord,Word.STATUS_CHECK_WRITE);

                doStep();
                return;
            } else {
                state = State.fail;
                fragment.showWord(randomWord, state, variants, rightVariant, choicedVariant);
                if (settings.isReadWords() && settings.isReadTranslate()) {
                    getTts().speak(randomWord.getTranslate(), settings.getSpeedReadTranslate(), Locale.getDefault(), null);
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
        fragment.showWord(randomWord, state,variants,rightVariant,choicedVariant);
    }

    private TTS getTts() {
        return ((MainActivity) context).getTts();
    }
}
