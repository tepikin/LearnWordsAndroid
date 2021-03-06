package ru.lazard.learnwords.ui.fragments.wordsList.words;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import ru.lazard.learnwords.model.Dictionary;
import ru.lazard.learnwords.model.Model;
import ru.lazard.learnwords.model.Word;

/**
 * Created by Egor on 03.06.2016.
 */
public class WordsListPresenter {
    private final WordsListFragment fragment;
    private List<Word> words = new ArrayList<>();
    private String searchQuery = "";

    public WordsListPresenter(WordsListFragment wordsListFragment) {
        this.fragment = wordsListFragment;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void init() {
        words = filterWordsByDictionary(Model.getInstance().getWords(), fragment.getDictionaryId());
        onSearch(searchQuery);
    }

    public static List<Word> filterWordsByDictionary(List<Word> wordsIn, int groupId) {
        if (groupId == Dictionary.NO_DICTIONARY_ID) {
            return new ArrayList<>(wordsIn);
        }
        List<Word> wordsInGroup= new ArrayList<>();
        for (Word word : Model.getInstance().getWords()) {
            if (word.getDictionaryId() == groupId)
                wordsInGroup.add(word);
        }
        return wordsInGroup;
    }

    public void onDestroy() {
        // TODO un implemented yaeat

    }

    public void onSearch(String query) {
        searchQuery = query;
        if (words == null) return;
        if (TextUtils.isEmpty(query)) {
            fragment.setList(words);
            return;
        }
        List<Word> list = new ArrayList<>();
        for (Word word : words) {
            if (word == null) continue;
            if (word.isContainsText(query)) {
                list.add(word);
            }
        }
        fragment.setList(list);
    }
}
