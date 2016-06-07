package ru.lazard.learnwords.model;

import android.text.TextUtils;

/**
 * Created by Egor on 02.06.2016.
 */
public class Word {
    private int id;
    private int status;
    private int difficulty;
    private String transcription;
    private String translate;
    private int viewCount;
    private boolean visible;
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
        return false;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
