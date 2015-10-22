package com.example.ak.dictionary.network.api;

import com.example.ak.dictionary.network.api.data.DictionaryResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Custom response deserializer
 */
public class DictionaryResponseDeserializer implements JsonDeserializer<DictionaryResponse> {

    @Override
    public DictionaryResponse deserialize(JsonElement jsonResponse, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = jsonResponse.getAsJsonObject();
        DictionaryResponse response = new DictionaryResponse();
        try {
            response.variants = new ArrayList<>();
            JsonArray body = jsonObject.getAsJsonArray("def");
            if (body != null) {
                for (int i = 0; i < body.size(); ++i) {
                    JsonArray tr = body.get(i).getAsJsonObject().getAsJsonArray("tr");
                    if (tr != null) {
                        for (int j=0; j<tr.size(); ++j) {
                            String text = tr.get(j).getAsJsonObject().get("text").getAsString();
                            response.variants.add(text);
                        }
                    }
                }
            }

            return response;

        } catch (Exception exception) {
            throw new JsonParseException(exception);
        }
    }
}