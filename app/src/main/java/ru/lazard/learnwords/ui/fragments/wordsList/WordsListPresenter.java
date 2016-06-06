package ru.lazard.learnwords.ui.fragments.wordsList;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import ru.lazard.learnwords.db.DAO;
import ru.lazard.learnwords.model.Word;

/**
 * Created by Egor on 03.06.2016.
 */
public class WordsListPresenter {
    private final WordsListFragment fragment;
    private List<Word> words = new ArrayList<>();

    public WordsListPresenter(WordsListFragment wordsListFragment) {
        this.fragment = wordsListFragment;

    }

    public void init() {
       words= DAO.getAllWords();
        fragment.setList(words);

    }

    public void onDestroy() {
        // TODO un implemented yaeat

    }

    public void onSearch(String query) {
        if (words==null)return;
        if (TextUtils.isEmpty(query)){
            fragment.setList(words);
            return;
        }
        List<Word> list= new ArrayList<>();
        for (Word word : words) {
            if (word ==null)continue;
            if (word.isContainsText(query)){
                list.add(word);
            }
        }
        fragment.setList(list);

    }
}
