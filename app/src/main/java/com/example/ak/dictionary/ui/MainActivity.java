package com.example.ak.dictionary.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.ak.dictionary.NavigationListener;
import com.example.ak.dictionary.R;
import com.example.ak.dictionary.ui.fragments.WordDetailsFragment;
import com.example.ak.dictionary.ui.fragments.WordsFragment;

public class MainActivity extends AppCompatActivity implements NavigationListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WordsFragment wordsFragment = new WordsFragment();
        setContent(wordsFragment);
    }

    @Override
    public void onBackPressed() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        super.onBackPressed();
        showBackButton(getFragmentManager().getBackStackEntryCount() > 0);
    }

    @Override
    public void showBackButton(boolean visible) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(visible);
        }
    }

    private void setContent(BaseAppFragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }

    private void showFragment(Fragment fragment) {
        showBackButton(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack(fragment.getClass().getSimpleName());
        ft.commit();
    }

    @Override
    public void setFragmentTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    public void goBack() {
        onBackPressed();
    }

    @Override
    public void navigateWordDetails(String word) {
        WordDetailsFragment fragment = WordDetailsFragment.newInstance(word);
        showFragment(fragment);
    }
}
