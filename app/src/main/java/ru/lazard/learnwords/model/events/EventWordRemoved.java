package ru.lazard.learnwords.model.events;

import ru.lazard.learnwords.model.Word;

/**
 * Created by Egor on 10.06.2016.
 */
public class EventWordRemoved {
    private final Word word;

    public Word getWord() {
        return word;
    }

    public EventWordRemoved(Word word) {
        this.word=word;

    }
}
