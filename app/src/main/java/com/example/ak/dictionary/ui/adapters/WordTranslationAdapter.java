package com.example.ak.dictionary.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ak.dictionary.R;
import com.example.ak.dictionary.db.model.Translation;

import java.util.List;

/**
 * Word translation recycler adapter
 */
public class WordTranslationAdapter extends RecyclerView.Adapter<WordTranslationAdapter.TranslationViewHolder>  {

    public class TranslationViewHolder extends RecyclerView.ViewHolder {

        private TextView mTranslationsText;

        public TranslationViewHolder(View view) {
            super(view);
            mTranslationsText = (TextView) itemView.findViewById(R.id.text_translations);
        }

        public void bind(final Translation translation) {
            mTranslationsText.setText(translation.getTranslation());
        }
    }

    private List<Translation> mTranslations;

    public WordTranslationAdapter(List<Translation> translations) {
        mTranslations = translations;
    }

    @Override
    public TranslationViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.partial_translation_list_item, viewGroup, false);
        return new TranslationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TranslationViewHolder wordViewHolder, int position) {
        Translation translation = mTranslations.get(position);
        if (translation != null) {
            wordViewHolder.bind(translation);
        }
    }

    @Override
    public int getItemCount() {
        return (mTranslations != null)? mTranslations.size(): 0;
    }
}
