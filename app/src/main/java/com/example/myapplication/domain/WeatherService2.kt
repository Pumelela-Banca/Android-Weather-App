package com.example.myapplication.domain

import com.example.myapplication.data.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherService2 {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}
