package com.example.ak.dictionary.db.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Word model
 */
public class Word extends RealmObject {
    private String word;
    private RealmList<Translation> translations;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public RealmList<Translation> getTranslations() {
        return translations;
    }

    public void setTranslations(RealmList<Translation> translations) {
        this.translations = translations;
    }
}
