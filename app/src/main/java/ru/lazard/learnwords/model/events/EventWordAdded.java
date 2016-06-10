package ru.lazard.learnwords.model.events;

import ru.lazard.learnwords.model.Word;

/**
 * Created by Egor on 10.06.2016.
 */
public class EventWordAdded {
    private final Word word;

    public EventWordAdded(Word word) {
        this.word=word;
    }

    public Word getWord() {
        return word;
    }
}
