package ru.lazard.learnwords.model.events;

import java.util.List;

import ru.lazard.learnwords.model.Word;

/**
 * Created by Egor on 10.06.2016.
 */
public class EventWordsListStatusChanged {
    private final List<Word> wordsList;

    public EventWordsListStatusChanged(List<Word> wordsList) {
        this.wordsList = wordsList;

    }
}
