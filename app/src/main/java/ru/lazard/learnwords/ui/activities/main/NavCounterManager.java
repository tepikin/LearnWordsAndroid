package ru.lazard.learnwords.ui.activities.main;

import android.text.TextUtils;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import ru.lazard.learnwords.R;
import ru.lazard.learnwords.model.Model;
import ru.lazard.learnwords.model.Word;
import ru.lazard.learnwords.ui.BaseActivity;

/**
 * Created by Egor on 10.06.2016.
 */
public class NavCounterManager implements BaseActivity.OnActivityDestroyListener {
    private MainActivity activity;

    public NavCounterManager(MainActivity activity) {
        this.activity = activity;
        activity.addOnActivityDestroyListener(this);
        EventBus.getDefault().register(this);
        updateCounters();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);

    }
    @Subscribe
    public void onModelUpdated(Model event){
        updateCounters();
    }
    public void updateCounters(){

        int noneWords = 0;
        int learnWords = 0;
        int translateWords = 0;
        int spellWords = 0;
        int readyWords = 0;

        for (Word word : Model.getInstance().getWords()) {
            switch (word.getStatus()){
                case  Word.STATUS_NONE:noneWords++;break;
                case  Word.STATUS_LEARN:learnWords++;break;
                case  Word.STATUS_CHECK_TRANSLATE:translateWords++;break;
                case  Word.STATUS_CHECK_WRITE:spellWords++;break;
                case  Word.STATUS_READY:readyWords++;break;
            }
        }

        MenuItem itemLearnWords = activity.getNavigationView().getMenu().findItem(R.id.nav_learnWords);
        MenuItem itemRepeat= activity.getNavigationView().getMenu().findItem(R.id.nav_repeatWords);
        MenuItem itemTranslateCheck= activity.getNavigationView().getMenu().findItem(R.id.nav_checkTranslate);
        MenuItem itemSpellCheck= activity.getNavigationView().getMenu().findItem(R.id.nav_spellcheck);

        setCounter(itemLearnWords,learnWords);
        setCounter(itemRepeat,learnWords);
        setCounter(itemTranslateCheck,translateWords);
        setCounter(itemSpellCheck,spellWords);

    }

    private void setCounter(MenuItem menuItem,int count){
        if (menuItem==null)return;
        CharSequence title = menuItem.getTitle();
        if (TextUtils.isEmpty(title))return;
        String text = title.toString().replaceAll("\\(.*\\)","");
        if (count>0) {
            text = text + " (" + count + ")";
        }
        menuItem.setTitle(text);
    }


}
