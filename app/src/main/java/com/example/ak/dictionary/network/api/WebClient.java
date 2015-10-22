package com.example.ak.dictionary.network.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

public class WebClient {
    private static final String API_KEY = "dict.1.1.20151022T090257Z.8feaf660666c5bd0.53c423cc2b01fe148950cdb01df6a0b0bb694e47";

    private static final String BASE_URL = "https://dictionary.yandex.net/api/v1/dicservice.json";
    private static final int CONNECT_TIMEOUT = 10;
    private static final int READ_WRITE_TIMEOUT = 10;

    private static Api mApi;

    static {
        Executor executor = Executors.newSingleThreadExecutor();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setClient(new OkClient(getHttpClient()))
                .setExecutors(executor, executor)
                .setErrorHandler(new CustomErrorHandler())
                .setConverter(new GsonConverter(getResponseConverter()))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addQueryParam("key", API_KEY);
                    }
                })
                .build();
        mApi = restAdapter.create(Api.class);
    }

    public static Api getApi() {
        return mApi;
    }

    private static Gson getResponseConverter() {
        return new GsonBuilder()
                .create();
    }

    private static OkHttpClient getHttpClient() {
        OkHttpClient client = new OkHttpClient();

        client.setConnectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        client.setReadTimeout(READ_WRITE_TIMEOUT, TimeUnit.SECONDS);
        client.setWriteTimeout(READ_WRITE_TIMEOUT, TimeUnit.SECONDS);

        return client;
    }

    private static class CustomErrorHandler implements ErrorHandler {

        @Override
        public Throwable handleError(RetrofitError error) {
            String errorMessage = null;

            if (error.getKind() == RetrofitError.Kind.NETWORK) {
                errorMessage = "A network error occurred while attempting to communicate with the server.";
            }
            else
            if (error.getResponse() == null) {
                errorMessage = "No response from the server.";
            }

            if (errorMessage != null) {
                return new Exception(errorMessage);
            }
            return new Exception(error.getMessage());
        }

    }

}
