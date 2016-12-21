package ru.lazard.learnwords.model.events;

import ru.lazard.learnwords.model.Dictionary;

/**
 * Created by Egor on 21.12.2016.
 */

public class EventDictionaryRemoved {
    private final Dictionary dictionary;

    public EventDictionaryRemoved(Dictionary dictionary) {
        this.dictionary=dictionary;
    }
}
