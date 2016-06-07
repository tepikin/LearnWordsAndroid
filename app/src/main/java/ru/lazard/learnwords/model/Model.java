package ru.lazard.learnwords.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ru.lazard.learnwords.utils.Utils;

/**
 * Created by Egor on 07.06.2016.
 */
public class Model {
    private static Model instance;
    private List<Word> words = new ArrayList<>();

    public Model(Context context) {
        // TODO un implemented yaeat

    }

    public static final Model getInstance() {
        if (instance == null) throw new IllegalStateException("call Model.init(); first;");
        return instance;
    }

    public static void init(Context context) {
        if (instance != null) return;
        instance = new Model(context);
    }

    public Word getRandomWordForLearning() {
        List<Word> wordsWithStatus = getWordsWithStatus(1);
        if (wordsWithStatus.size() <= 0) return null;
        int randomInt = Utils.randomInt(wordsWithStatus.size());
        return wordsWithStatus.get(randomInt);
    }

    public List<Word> getWords() {
        return words;
    }

    public void setLearnWordsListByDifficult(int difficulty) {
        for (Word word : words) {
            if (word.getStatus()==Word.STATUS_NONE||word.getStatus()==Word.STATUS_LEARN) {
                    word.setStatus(word.getDifficulty()==difficulty?Word.STATUS_LEARN:Word.STATUS_NONE);
            }
        }
    }

    public void setLearnWordsListClear() {
        for (Word word : words) {
            if (word.getStatus()==Word.STATUS_NONE||word.getStatus()==Word.STATUS_LEARN) {
                word.setStatus(Word.STATUS_NONE);
            }
        }
    }
    public void setLearnWordsListAll() {
        for (Word word : words) {
            if (word.getStatus()==Word.STATUS_NONE||word.getStatus()==Word.STATUS_LEARN) {
                word.setStatus(Word.STATUS_LEARN);
            }
        }
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    public List<Word> getWordsWithStatus(int status) {
        List<Word> wordsByStatus = new ArrayList<>();
        for (Word word : words) {
            if (word.getStatus() == status) {
                wordsByStatus.add(word);
            }
        }
        return wordsByStatus;
    }
}
