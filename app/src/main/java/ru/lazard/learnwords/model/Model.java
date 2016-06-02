package ru.lazard.learnwords.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Egor on 02.06.2016.
 */
public class Model {
    private List<Word> words = new ArrayList<>();

    public List<Word> getWords() {
        return words;
    }
    public Word getRandomWord(){
        int random = (int)(Math.random()*(getWords().size()-1));
        return words.get(random);
    }
}
