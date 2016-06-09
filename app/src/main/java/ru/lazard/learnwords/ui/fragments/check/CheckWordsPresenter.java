package ru.lazard.learnwords.ui.fragments.check;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;
import java.util.Locale;

import ru.lazard.learnwords.db.DAO;
import ru.lazard.learnwords.model.Model;
import ru.lazard.learnwords.model.Word;
import ru.lazard.learnwords.speach.TTS;
import ru.lazard.learnwords.ui.activities.main.MainActivity;
import ru.lazard.learnwords.ui.fragments.check.CheckWordsFragment.State;
import ru.lazard.learnwords.ui.fragments.preferences.Settings;


public class CheckWordsPresenter implements FragmentManager.OnBackStackChangedListener{
    private final CheckWordsFragment fragment;
    private final Context context;
    private Word randomWord;
    private State state= State.start;
    private Settings settings;

    public CheckWordsPresenter(CheckWordsFragment mainActivity) {
        this.fragment = mainActivity;
        context = fragment.getContext();
        settings = new Settings(context);
        fragment.getActivity().getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    public void doStep() {
        state=State.start;
        randomWord = Model.getInstance().getRandomWordForLearning();
        fragment.showWord(randomWord,state);
        if (settings.isReadWords()&&randomWord!=null){
            getTts().speak(randomWord.getWord(),settings.speedReadWords(), Locale.ENGLISH,null);
        }
    }
    public void onDestroy() {
        getTts().stop();
        fragment.getActivity().getSupportFragmentManager().removeOnBackStackChangedListener(this);
    }
    public void onApplyViewClick() {
        if (randomWord==null)return;
       if (state== State.start){
           randomWord.setStatus(Word.STATUS_CHECK_1);
           DAO.setStatusForWord(randomWord.getId(),Word.STATUS_CHECK_1);
           state = State.success;
           fragment.showWord(randomWord,state);
           if (settings.isReadWords()&&settings.isReadTranslate()){
               getTts().speak(randomWord.getTranslate(),settings.speedReadTranslate(), Locale.getDefault(),null);
           }

       }else
        if (state== State.fail){
            randomWord.setStatus(Word.STATUS_CHECK_1);
            DAO.setStatusForWord(randomWord.getId(),Word.STATUS_CHECK_1);
            doStep();

        }else
        if (state== State.success){
            doStep();
        }
    }
    @Override
    public void onBackStackChanged() {
        if (fragment==null)return;
        List<Fragment> fragments = fragment.getActivity().getSupportFragmentManager().getFragments();
        if (fragments!=null&&fragments.size()>0){
            Fragment topFragment = fragments.get(fragments.size() - 1);
            if (topFragment!=fragment){
                getTts().stop();
            }
        }
    }
    public void onCancelViewClick() {
        if (randomWord==null)return;
        if (state== State.start){
            state = State.fail;
            fragment.showWord(randomWord,state);
            if (settings.isReadWords()&&settings.isReadTranslate()){
                getTts().speak(randomWord.getTranslate(),settings.speedReadTranslate(), Locale.getDefault(),null);
            }
        }else
        if (state== State.fail){
            doStep();
        }else
        if (state== State.success){
            randomWord.setStatus(Word.STATUS_LEARN);
            DAO.setStatusForWord(randomWord.getId(),Word.STATUS_LEARN);
            doStep();
        }
    }

    public void onNextViewClick() {
        if (randomWord==null)return;
        if (state== State.start){
            doStep();
        }else
        if (state== State.fail){
            doStep();
        }else
        if (state== State.success){
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


    public void restoreState() {
        fragment.setSoundEnable(settings.isReadWords());
        if (randomWord == null) {
            doStep();
            return;
        }
        fragment.showWord(randomWord,state);


    }

    private TTS getTts() {
        return ((MainActivity) context).getTts();
    }

}
