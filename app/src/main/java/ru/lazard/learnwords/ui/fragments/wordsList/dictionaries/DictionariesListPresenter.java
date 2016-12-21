package ru.lazard.learnwords.ui.fragments.wordsList.dictionaries;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import ru.lazard.learnwords.model.Dictionary;
import ru.lazard.learnwords.model.Model;

/**
 * Created by Egor on 03.06.2016.
 */
public class DictionariesListPresenter {
    private final DictionariesListFragment fragment;
    private List<Dictionary> dictionaries = new ArrayList<>();
    private String searchQuery = "";

    public DictionariesListPresenter(DictionariesListFragment wordsListFragment) {
        this.fragment = wordsListFragment;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void init() {
        dictionaries = Model.getInstance().getDictionaries();
        onSearch(searchQuery);
    }


    public void onDestroy() {
        // TODO un implemented yaeat

    }

    public void onSearch(String query) {
        searchQuery = query;
        if (dictionaries == null) return;
        if (TextUtils.isEmpty(query)) {
            fragment.setList(dictionaries);
            return;
        }
        List<Dictionary> list = new ArrayList<>();
        for (Dictionary dictionary : dictionaries) {
            if (dictionary == null) continue;
            if (dictionary.isContainsText(query)) {
                list.add(dictionary);
            }
        }
        fragment.setList(list);
    }
}
