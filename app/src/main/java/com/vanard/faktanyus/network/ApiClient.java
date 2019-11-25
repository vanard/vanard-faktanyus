package com.vanard.faktanyus.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static final String BASE_URL = " https://numbersapi.p.rapidapi.com/";
    public static final String APIXU_BASE_URL = "https://api.apixu.com/v1/";
    public static final String OPENWEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/";
    public static final String OPENWEATHER_IMAGE_URL = "https://openweathermap.org/img/w/";
    public static final String OPENWEATHER_API_KEY = "6eac656e9e89aba987e082468e7b534a";

    private static Retrofit retrofit = null;
    public static Retrofit getClient(String url) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static void clearClient() {
        retrofit = null;
    }
}
