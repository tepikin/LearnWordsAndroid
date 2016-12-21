package ru.lazard.learnwords.model;

import android.text.TextUtils;

/**
 * Created by Egor on 21.12.2016.
 */

public class Dictionary {
    public static final int NO_DICTIONARY_ID = -1;
    private String name;
    private int id;

    public Dictionary(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public Dictionary() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isContainsText(String query) {
        if (TextUtils.isEmpty(query))return true;
        if (TextUtils.isEmpty(name))return false;
        if (name.toLowerCase().contains(query.toLowerCase()))return true;
        return false;
    }

}
