package com.example.ak.dictionary.network.api;

import com.example.ak.dictionary.network.api.data.DictionaryResponse;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Yandex translate API
 */
public interface Api {
    @GET("/lookup?lang=en-ru")
    DictionaryResponse getTranslation(@Query("text") String text);
}
