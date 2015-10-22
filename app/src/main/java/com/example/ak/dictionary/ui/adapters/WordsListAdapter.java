package com.example.ak.dictionary.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ak.dictionary.R;
import com.example.ak.dictionary.db.model.Word;

import java.util.List;

/**
 * Words recycler adapter
 */
public class WordsListAdapter extends RecyclerView.Adapter<WordsListAdapter.WordViewHolder>  {

    public class WordViewHolder extends RecyclerView.ViewHolder {

        private TextView mWordText;
        private TextView mTranslationsText;

        public WordViewHolder(View view) {
            super(view);
            mWordText = (TextView) itemView.findViewById(R.id.text_word);
            mTranslationsText = (TextView) itemView.findViewById(R.id.text_translations);
        }

        public void bind(final Word word) {
            mWordText.setText(word.getWord());
            if (word.getTranslations().isEmpty()) {
                mTranslationsText.setText(R.string.no_translation);
            }
            else {
                String translations = word.getTranslations().first().getTranslation();
                for (int i=1; i<word.getTranslations().size(); ++i) {
                    translations += ", " + word.getTranslations().get(i).getTranslation();
                }
                mTranslationsText.setText(translations);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onWordSelected(word);
                }
            });
        }
    }

    public interface WordSelectListener {
        void onWordSelected(Word word);
    }

    private WordSelectListener mListener;
    private List<Word> mWords;

    public WordsListAdapter(WordSelectListener listener) {
        mListener = listener;
    }

    public void setWords(List<Word> words) {
        mWords = words;
        notifyDataSetChanged();
    }

    @Override
    public WordViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.partial_word_list_item, viewGroup, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WordViewHolder wordViewHolder, int position) {
        Word word = mWords.get(position);
        if (word != null) {
            wordViewHolder.bind(word);
        }
    }

    @Override
    public int getItemCount() {
        return (mWords != null)? mWords.size(): 0;
    }
}
