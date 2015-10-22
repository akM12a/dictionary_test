package com.example.ak.dictionary.network.api.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * API response model
 */
public class DictionaryResponse {
    @SerializedName("def")
    public List<DictionaryPart> parts;
}
