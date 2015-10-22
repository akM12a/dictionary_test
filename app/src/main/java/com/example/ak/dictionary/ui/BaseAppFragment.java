package com.example.ak.dictionary.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MenuItem;

import com.example.ak.dictionary.NavigationListener;
import com.example.ak.dictionary.R;
import com.example.ak.dictionary.db.WordsStore;
import com.example.ak.dictionary.db.WordsStoreRealm;
import com.example.ak.dictionary.network.service.ServiceTask;

import java.util.concurrent.Callable;

/**
 * All app fragments extends this class
 */
public abstract class BaseAppFragment extends Fragment {

    protected interface WordsStoreQuery<T> {
        T query(WordsStore wordsStore) throws Exception;
    }

    protected interface CompleteListener<T> {
        void onComplete(T result);
    }

    protected interface ErrorListener {
        void onError(Exception e);
    }

    private WordsStore mWordsStore;
    protected NavigationListener mNavigationListener;
    private ProgressDialog mProgressDialog;

    protected abstract String fragmentTitle();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavigationListener) {
            mNavigationListener = (NavigationListener) context;
        }
        else {
            throw new IllegalStateException("Activity not implement NavigationListener");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mNavigationListener.setFragmentTitle(this.fragmentTitle());
    }

    @Override
    public void onDestroy() {
        if (mWordsStore != null) {
            disconnectFromWordsStore(mWordsStore, null);
        }
        super.onDestroy();
    }

    protected void connectToWordsStore(CompleteListener<WordsStore> completeListener, final ErrorListener errorListener) {
        try {
            if (completeListener != null) {
                completeListener.onComplete(new WordsStoreRealm(getContext()));
            }
        }
        catch (final Exception e) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.error_header)
                    .setMessage(R.string.database_connection_error)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            if (errorListener != null) {
                                errorListener.onError(e);
                            }
                        }
                    });
            alertDialog.show();
        }
    }

    protected void disconnectFromWordsStore(WordsStore wordsStore, ErrorListener errorListener) {
        try {
            wordsStore.close();
        }
        catch (Exception e) {
            if (errorListener != null) {
                errorListener.onError(e);
            }
        }
    }

    protected <T> void executeDatabaseQuery(final WordsStoreQuery<T> wordsStoreQuery, final CompleteListener<T> callback,
                                 final ErrorListener errorListener) {
            if (mWordsStore == null) {
                connectToWordsStore(new CompleteListener<WordsStore>() {
                    @Override
                    public void onComplete(WordsStore result) {
                        mWordsStore = result;
                        executeDatabaseQuery(mWordsStore, wordsStoreQuery, callback, errorListener);
                    }
                }, errorListener);
            }
            else {
                executeDatabaseQuery(mWordsStore, wordsStoreQuery, callback, errorListener);
            }
    }

    private <T> void executeDatabaseQuery(final WordsStore wordsStore, final WordsStoreQuery<T> wordsStoreQuery,
                                          final CompleteListener<T> callback, final ErrorListener errorListener) {
        try {
            T result = wordsStoreQuery.query(wordsStore);
            callback.onComplete(result);
        }
        catch (final Exception e) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.error_header)
                    .setMessage(e.getMessage())
                    .setCancelable(false)
                    .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            executeDatabaseQuery(wordsStore, wordsStoreQuery, callback, errorListener);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (errorListener != null) {
                                errorListener.onError(e);
                            }
                        }
                    });
            alertDialog.show();
        }
    }

    protected void showProgress(boolean visible) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        if (visible) {
            mProgressDialog = ProgressDialog.show(getContext(), null, getString(R.string.translating));
        }
    }

    private <T> void handleServiceError(Exception error, final Class<T> resultType,
                                        final Callable<T> request, final CompleteListener<T> listener) {
        String errorMessage;
        if ((error.getCause() != null) && (error.getCause().getMessage() != null)) {
            errorMessage = error.getCause().getMessage();
        }
        else {
            errorMessage = error.getMessage();
            if (TextUtils.isEmpty(errorMessage)) {
                errorMessage = getString(R.string.unknown_error);
            }
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity())
                .setMessage(errorMessage)
                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        executeServiceTask(resultType, request, listener);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null);
        alertDialog.show();
    }

    protected <T> void executeServiceTask(final Class<T> resultType, final Callable<T> request,
                                          final CompleteListener<T> listener) {

        ServiceTask<T> task = new ServiceTask<>(resultType, request);

        task.setListener(new ServiceTask.ServiceListener<T>() {
            @Override
            public void onTaskComplete(T result) {
                showProgress(false);
                if (listener != null) {
                    if (!isRemoving() && !isDetached() && isAdded()) {
                        listener.onComplete(result);
                    }
                }
            }

            @Override
            public void onTaskError(Exception error) {
                showProgress(false);
                handleServiceError(error, resultType, request, listener);
            }
        });

        showProgress(true);
        task.execute();
    }
}
