package ru.lazard.learnwords.model;

import android.text.TextUtils;

/**
 * Created by Egor on 02.06.2016.
 */
public class Word {
    public static final int STATUS_NONE = 0;
    public static final int STATUS_LEARN = 1;
    public static final int STATUS_CHECK_TRANSLATE = 2;
    public static final int STATUS_CHECK_WRITE = 3;
    public static final int STATUS_READY = 4;
    private int dictionaryId;
    private int id;
    private int status;
    private String transcription;
    private String translate;
    private int viewCount;

    private String word;

    public Word() {

    }

    public Word(int status, String transcription, String translate, String word) {
        this.status = status;
        this.transcription = transcription;
        this.translate = translate;
        this.word = word;
    }


    public Word(int dictionaryId, int id, int status, String transcription, String translate, int viewCount, String word) {
        this.dictionaryId = dictionaryId;
        this.id = id;
        this.status = status;
        this.transcription = transcription;
        this.translate = translate;
        this.viewCount = viewCount;
        this.word = word;
    }

    public Word(String word, String translate) {
        this.word = word;
        this.translate = translate;
    }

    public Word(Word word) {
       this.dictionaryId =word.dictionaryId;
       this.id=word.id;
       this.status=word.status;
       this.transcription=word.transcription;
       this.translate=word.translate;
       this.viewCount=word.viewCount;
       this.word=word.word;
    }

    public int getDictionaryId() {
        return dictionaryId;
    }

    public void setDictionaryId(int dictionaryId) {
        this.dictionaryId = dictionaryId;
    }

    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    void setStatus(int status) {
        this.status = status;
    }

    public String getTranscription() {
        return transcription;
    }

    void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public String getTranslate() {
        return translate;
    }
    public String getTranslateShort() {
        if (translate==null)return translate;
        int index = translate.indexOf(";");
        if (index >0)return translate.substring(0,index);

        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    public int getViewCount() {
        return viewCount;
    }

    void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public boolean isContainsText(String text) {
if (TextUtils.isEmpty(text))return true;
        if (!TextUtils.isEmpty(word) && word.toLowerCase().contains(text.toLowerCase())) {
            return true;
        }
        if (!TextUtils.isEmpty(translate) && translate.toLowerCase().contains(text.toLowerCase())) {
            return true;
        }
        if (!TextUtils.isEmpty(transcription) && transcription.toLowerCase().contains(text.toLowerCase())) {
            return true;
        }
        return false;
    }
}
