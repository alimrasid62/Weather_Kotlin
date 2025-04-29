package com.alimrasid.cuaca_1.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather/realtime")
    fun getRealtimeWeather(
        @Query("location") location: String,
        @Query("apikey") apiKey: String
    ): Call<WeatherResponse>
}