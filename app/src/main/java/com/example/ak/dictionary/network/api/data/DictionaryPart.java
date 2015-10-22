package com.example.ak.dictionary.network.api.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by AK on 22.10.2015.
 */
public class DictionaryPart {
    @SerializedName("tr")
    public List<DictionaryVariant> variants;
}
