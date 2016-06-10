package ru.lazard.learnwords.model.events;

import ru.lazard.learnwords.model.Word;

/**
 * Created by Egor on 10.06.2016.
 */
public class EventWordStatusChanged {
    private int fromStatus;
    private int toStatus;
    private Word word;

    public int getFromStatus() {
        return fromStatus;
    }

    public int getToStatus() {
        return toStatus;
    }

    public Word getWord() {
        return word;
    }

    public EventWordStatusChanged(int fromStatus, int toStatus, Word word) {
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.word = word;
    }
}
