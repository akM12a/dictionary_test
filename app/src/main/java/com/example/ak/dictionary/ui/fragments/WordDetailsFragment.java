package com.example.ak.dictionary.ui.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.ak.dictionary.R;
import com.example.ak.dictionary.db.WordsStore;
import com.example.ak.dictionary.db.model.Word;
import com.example.ak.dictionary.ui.BaseAppFragment;
import com.example.ak.dictionary.ui.adapters.WordTranslationAdapter;

/**
 * Word details fragment
 */
public class WordDetailsFragment extends BaseAppFragment {
    private static final String ARG_WORD = "word";

    public static WordDetailsFragment newInstance(String word) {
        WordDetailsFragment wordDetailsFragment = new WordDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_WORD, word);
        wordDetailsFragment.setArguments(args);
        return wordDetailsFragment;
    }

    private String mWord;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle args = getArguments();
        if (args != null) {
            mWord = args.getString(ARG_WORD);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_word_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        final View noTranslationMessage = view.findViewById(R.id.text_no_translation_message);

        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_translations);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        executeDatabaseQuery(new WordsStoreQuery<Word>() {
            @Override
            public Word query(WordsStore wordsStore) throws Exception {
                Word word = wordsStore.getWord(mWord);
                if (word == null) {
                    throw new Exception(getString(R.string.word_not_found));
                }
                return word;
            }
        }, new CompleteListener<Word>() {
            @Override
            public void onComplete(Word result) {
                noTranslationMessage.setVisibility(result.getTranslations().isEmpty()? View.VISIBLE: View.INVISIBLE);
                recyclerView.setAdapter(new WordTranslationAdapter(result.getTranslations()));
            }
        }, new ErrorListener() {
            @Override
            public void onError(Exception e) {
                mNavigationListener.goBack();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_word_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteWord();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteWord() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.delete_word_header)
                .setMessage(R.string.delete_word_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        executeDatabaseQuery(new WordsStoreQuery<Void>() {
                            @Override
                            public Void query(WordsStore wordsStore) throws Exception {
                                wordsStore.deleteWord(mWord);
                                return null;
                            }
                        }, new CompleteListener<Void>() {
                            @Override
                            public void onComplete(Void result) {
                                mNavigationListener.goBack();
                            }
                        }, null);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null);
        alertDialog.show();
    }

    @Override
    protected String fragmentTitle() {
        return mWord;
    }
}
