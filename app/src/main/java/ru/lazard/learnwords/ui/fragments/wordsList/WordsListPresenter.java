package ru.lazard.learnwords.ui.fragments.wordsList;

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
}
