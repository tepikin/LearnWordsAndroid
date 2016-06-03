package ru.lazard.learnwords.model;

/**
 * Created by Egor on 02.06.2016.
 */
public class Word {
    private String word;
    private String translate;
    private int viewCount;
    private int id;
    private boolean visible;

    public Word() {

    }
    public Word(String word, String translate) {
        this.word = word;
        this.translate = translate;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }
}
