package com.example.myapplication.utils

import com.example.myapplication.data.DailyForecastSummary
import com.example.myapplication.data.ForecastResponse


fun ForecastResponse.toDailySummary() : List<DailyForecastSummary> {

    // Group forecasts by date (yyyy-mm-dd from dt_txt)

    val grouped = list.groupBy { it.dt_txt.substring(0, 10) }

    return grouped.entries.map { (date, forecasts) ->
        val avgTemp = forecasts.map { it.main.temp }.average()
        val description = forecasts[forecasts.size / 2].weather.firstOrNull()?.description ?: "N/A"
        val icon = forecasts[forecasts.size / 2].weather.firstOrNull()?.icon ?: "01d"

        DailyForecastSummary(
            date = date,
            avgTemp = avgTemp,
            description = description,
            icon = icon
        )
    }.take(5) // limit to 5 days
}
