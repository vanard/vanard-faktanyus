package com.vanard.faktanyus.network;

import com.vanard.faktanyus.models.openweather.OpenWeatherResponse;
import com.vanard.faktanyus.models.rapidapi.DateFactResponse;
import com.vanard.faktanyus.models.rapidapi.YearFactResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @Headers({
            "x-rapidapi-host: numbersapi.p.rapidapi.com",
            "x-rapidapi-key: c474020896msh685205d6da8cbd8p1359fdjsn915617a2042f"
    })
    @GET("{year}/year")
    Call<YearFactResponse> getYearFact(@Path("year") String year,
                                       @Query("fragment") String fragment,
                                       @Query("json") String json);

    @Headers({
            "x-rapidapi-host: numbersapi.p.rapidapi.com",
            "x-rapidapi-key: c474020896msh685205d6da8cbd8p1359fdjsn915617a2042f"
    })
    @GET("{month}/{day}/date")
    Call<DateFactResponse> getDateFact(@Path("month") String month,
                                       @Path("day") String day,
                                       @Query("fragment") String fragment,
                                       @Query("json") String json);

    @GET("weather")
    Call<OpenWeatherResponse> getWeather(@Query("q") String city,
                                          @Query("appid") String appid);
}
