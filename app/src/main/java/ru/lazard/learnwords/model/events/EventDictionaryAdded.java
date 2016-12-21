package ru.lazard.learnwords.model.events;

import ru.lazard.learnwords.model.Dictionary;

/**
 * Created by Egor on 21.12.2016.
 */

public class EventDictionaryAdded {
    private final Dictionary dictionary;

    public EventDictionaryAdded(Dictionary dictionary) {
        this.dictionary=dictionary;
    }
}
