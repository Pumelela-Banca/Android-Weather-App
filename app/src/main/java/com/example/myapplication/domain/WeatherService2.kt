package com.example.myapplication.domain

import com.example.myapplication.data.ForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherService2 {
    @GET("data/2.5/forecast")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): ForecastResponse
}
