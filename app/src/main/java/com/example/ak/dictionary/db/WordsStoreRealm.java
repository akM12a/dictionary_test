package com.example.ak.dictionary.db;

import android.content.Context;

import com.example.ak.dictionary.db.model.Translation;
import com.example.ak.dictionary.db.model.Word;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * Words CRUD implementation
 */
public class WordsStoreRealm implements WordsStore {

    private Realm sRealm;

    public WordsStoreRealm(Context context) {
        sRealm = Realm.getInstance(context);
    }

    @Override
    public void close() {
        sRealm.close();
    }

    @Override
    public void addWord(String word, List<String> translations) {
        sRealm.beginTransaction();
        Word wordRealm = sRealm.createObject(Word.class);
        wordRealm.setWord(word);

        for (String translation: translations) {
            Translation translationRealm = sRealm.createObject(Translation.class);
            translationRealm.setTranslation(translation);
            wordRealm.getTranslations().add(translationRealm);
        }

        sRealm.commitTransaction();
    }

    @Override
    public List<Word> getWords() {
        return getWords(null);
    }

    @Override
    public List<Word> getWords(String filter) {
        RealmQuery<Word> query = sRealm.where(Word.class);
        if ((filter != null) && (filter.length() > 0)) {
            query = query.contains("word", filter, false).or().contains("translations.translation", filter, false);
        }
        return query.findAll();
    }

    @Override
    public void deleteWord(String word) {
        sRealm.beginTransaction();
        RealmQuery<Word> query = sRealm.where(Word.class).equalTo("word", word, false);
        query.findAll().clear();
        sRealm.commitTransaction();
    }

    @Override
    public Word getWord(String word) {
        RealmQuery<Word> query = sRealm.where(Word.class).equalTo("word", word, false);
        return query.findFirst();
    }
}
