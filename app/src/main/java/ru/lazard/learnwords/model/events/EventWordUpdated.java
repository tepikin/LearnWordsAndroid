package ru.lazard.learnwords.model.events;

import ru.lazard.learnwords.model.Word;

/**
 * Created by Egor on 10.06.2016.
 */
public class EventWordUpdated {
    private final Word fromWord;
    private final Word toWord;

    public Word getFromWord() {
        return fromWord;
    }

    public Word getToWord() {
        return toWord;
    }

    public EventWordUpdated(Word fromWord, Word toWord) {
        this.fromWord=fromWord;
        this.toWord=toWord;

    }
}
