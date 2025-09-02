package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.example.myapplication.domain.WeatherService2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : ComponentActivity() {

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    val userAPI: String? = null

    // Retrofit instance
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val weatherService = retrofit.create(WeatherService2::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //  Get Api-key from user
        if (userAPI == null) {

        } else {
            Log.d("MainActivity", "API key is $userAPI")
        }

        // Get users location





        // Launch a coroutine to fetch weather data
        // weatherService.getCurrentWeather(40.7128, -74.0060, "YOUR_API_KEY")
        CoroutineScope(Dispatchers.IO).launch {
            val weatherResponse = weatherService.getCurrentWeather(40.7128, -74.0060, "YOUR_API_KEY")
            Log.d("WeatherResponse", weatherResponse.toString())
        }

    }

}
