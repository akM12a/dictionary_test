package com.example.ak.dictionary.db.model;

import io.realm.RealmObject;

/**
 * Translation model
 */
public class Translation extends RealmObject {
    private String translation;

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }
}
