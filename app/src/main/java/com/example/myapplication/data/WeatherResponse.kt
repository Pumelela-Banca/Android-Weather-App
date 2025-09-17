package com.example.myapplication.data

data class ForecastResponse(
    val list: List<ForecastItem>,
    val city: City
)

data class ForecastItem(
    val dt: Long,
    val main: MainWeather,
    val weather: List<WeatherDescription>,
    val dt_txt: String
)

data class MainWeather(
    val temp: Double,
    val feels_like: Double
)

data class WeatherDescription(
    val description: String,
    val icon: String
)

data class City(
    val name: String,
    val country: String
)


data class DailyForecastSummary(
    val date: String,            //  "2025-09-17"
    val avgTemp: Double,         // average temperature for the day
    val description: String,     // main description (e.g. "cloudy")
    val icon: String             // icon code for weather image
)
