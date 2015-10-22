package com.example.ak.dictionary.db;

import com.example.ak.dictionary.db.model.Word;

import java.util.List;

/**
 * Words CRUD
 */
public interface WordsStore {
    void addWord(String word, List<String> translations);
    List<Word> getWords();
    List<Word> getWords(String filter);
    void deleteWord(String word);
    Word getWord(String word);
    void close();
}
