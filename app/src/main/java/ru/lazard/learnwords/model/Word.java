package ru.lazard.learnwords.model;

import android.text.TextUtils;

/**
 * Created by Egor on 02.06.2016.
 */
public class Word {
    public static final int STATUS_NONE = 0;
    public static final int STATUS_LEARN = 1;
    public static final int STATUS_CHECK_1 = 2;
    public static final int STATUS_CHECK_2 = 3;
    public static final int STATUS_CHECK_3 = 4;
    public static final int STATUS_READY = 5;
    private int id;
    private int status;
    private int difficulty;
    private String transcription;
    private String translate;
    private int viewCount;

    private String word;

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public Word() {

    }

    public Word(String word, String translate) {
        this.word = word;
        this.translate = translate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public boolean isContainsText(String text) {

        if (!TextUtils.isEmpty(word) && word.contains(text)) {
            return true;
        }
        if (!TextUtils.isEmpty(translate) && translate.contains(text)) {
            return true;
        }
        if (!TextUtils.isEmpty(transcription) && transcription.contains(text)) {
            return true;
        }
        return false;
    }


}
