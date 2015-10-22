package com.example.ak.dictionary;

/**
 * App navigation
 */
public interface NavigationListener {
    void goBack();

    void navigateWordDetails(String word);
    void setFragmentTitle(String title);
    void showBackButton(boolean visible);
}
