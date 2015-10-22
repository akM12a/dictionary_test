package com.example.ak.dictionary.ui.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ak.dictionary.R;
import com.example.ak.dictionary.db.WordsStore;
import com.example.ak.dictionary.db.model.Word;
import com.example.ak.dictionary.network.api.WebClient;
import com.example.ak.dictionary.network.api.data.DictionaryResponse;
import com.example.ak.dictionary.ui.BaseAppFragment;
import com.example.ak.dictionary.ui.adapters.WordsListAdapter;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Fragment for display words list
 */
public class WordsFragment extends BaseAppFragment {

    private WordsListAdapter mAdapter;
    private SearchView mSearchView;
    private TextView mNoWordsMessage;
    private boolean mDataWithFilter;

    private String mFilterText;

    public WordsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_words, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        FloatingActionButton floatingActionButton = (FloatingActionButton) view.findViewById(R.id.button_add_word);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = View.inflate(getContext(), R.layout.text_input_layout, null);
                final EditText input = (EditText) view.findViewById(R.id.edit_input);

                final AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                .setTitle(R.string.type_word)
                                .setView(view)
                                .setPositiveButton(android.R.string.ok, null)
                                .setNegativeButton(android.R.string.cancel, null)
                                .create();

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                String text = input.getText().toString().trim();
                                if (!TextUtils.isEmpty(text)) {
                                    alertDialog.dismiss();
                                    translateWord(text);
                                }
                            }
                        });
                    }
                });

                alertDialog.show();
            }
        });

        mNoWordsMessage = (TextView) view.findViewById(R.id.text_no_words_message);
        mNoWordsMessage.setVisibility(View.INVISIBLE);

        final RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_words);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (mAdapter == null) {
            mAdapter = new WordsListAdapter(new WordsListAdapter.WordSelectListener() {
                @Override
                public void onWordSelected(Word word) {
                    mNavigationListener.navigateWordDetails(word.getWord());
                }
            });
            updateData();
        }
        updateNoWordsMessage();
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_words, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                updateData();
                return true;
            }
        });

        mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setQueryHint(getString(R.string.search_hint));

        if (mFilterText != null) {
            String query = mFilterText;
            searchMenuItem.expandActionView();
            mSearchView.setQuery(query, true);
            mSearchView.clearFocus();
        }
        setSearchViewTextChangeListener();
    }

    @Override
    protected String fragmentTitle() {
        return getString(R.string.fragment_words_title);
    }

    @Override
    public void onResume() {
        super.onResume();
        setSearchViewTextChangeListener();
    }

    @Override
    public void onStop() {
        mSearchView.setOnQueryTextListener(null);
        super.onStop();
    }

    private void updateData() {
        updateData(null);
    }

    private void updateData(final String filter) {
        executeDatabaseQuery(new WordsStoreQuery<List<Word>>() {
            @Override
            public List<Word> query(WordsStore wordsStore) throws Exception {
                if (!TextUtils.isEmpty(filter)) {
                    return wordsStore.getWords(filter);
                } else {
                    return wordsStore.getWords();
                }
            }
        }, new CompleteListener<List<Word>>() {
            @Override
            public void onComplete(List<Word> result) {
                mAdapter.setWords(result);
                mDataWithFilter = !TextUtils.isEmpty(filter);
                updateNoWordsMessage();
                mFilterText = filter;
            }
        }, null);
    }

    private void translateWord(final String word) {
        executeServiceTask(DictionaryResponse.class, new Callable<DictionaryResponse>() {
            @Override
            public DictionaryResponse call() throws Exception {
                return WebClient.getApi().getTranslation(word);
            }
        }, new CompleteListener<DictionaryResponse>() {
            @Override
            public void onComplete(final DictionaryResponse result) {
                executeDatabaseQuery(new WordsStoreQuery<Void>() {
                    @Override
                    public Void query(WordsStore wordsStore) throws Exception {
                        if (result.variants.isEmpty()) {
                            throw new Exception(getString(R.string.word_not_found));
                        }
                        if (wordsStore.getWord(word) != null) {
                            throw new Exception(getString(R.string.you_already_added_this_word));
                        }

                        wordsStore.addWord(word, result.variants);
                        return null;
                    }
                }, new CompleteListener<Void>() {
                    @Override
                    public void onComplete(Void result) {
                        mAdapter.notifyDataSetChanged();
                    }
                }, null);
            }
        });
    }

    private void updateNoWordsMessage() {
        mNoWordsMessage.setText(mDataWithFilter ? R.string.no_results : R.string.you_have_no_words_yet);
        mNoWordsMessage.setVisibility((mAdapter.getItemCount() == 0) ? View.VISIBLE : View.INVISIBLE);
    }

    private void setSearchViewTextChangeListener() {
        if (mSearchView != null) {
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    updateData(s);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    updateData(s);
                    return true;
                }
            });
        }
    }
}
