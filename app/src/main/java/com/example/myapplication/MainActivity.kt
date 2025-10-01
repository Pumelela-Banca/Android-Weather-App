package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.domain.WeatherService2
import com.example.myapplication.userinterface.ForecastAdapter
import com.example.myapplication.utils.ApiKeyManager
import com.example.myapplication.utils.toDailySummary
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private var userAPI: String? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var forecastAdapter: ForecastAdapter



    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getUserLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    // Retrofit instance
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val weatherService = retrofit.create(WeatherService2::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Load 1")

        setContentView(R.layout.activity_main) //
        val apiKeyManager = ApiKeyManager(this)

        forecastAdapter = ForecastAdapter(emptyList())

        val recyclerView = findViewById<RecyclerView>(R.id.rvForecast)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = forecastAdapter
        Log.d(TAG, "Load 2")

        // Create button to show dialog and run function
        // ToDo

        val addApi: Button = findViewById<Button>(R.id.addApi)

        addApi.setOnClickListener {
            startApiDialog(apiKeyManager)
        }

        // Request location
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getUserLocation()
            }
            shouldShowRequestPermissionRationale(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                Toast.makeText(
                    this, "Location is needed to show weather for your area.",
                    Toast.LENGTH_LONG
                ).show()
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        // Get the items from API and update UI
        if (userAPI != null)
        {
            getWeatherItems()
        }

    }

    // Starts dialog to get API from user
    private fun startApiDialog(apiKeyManager : ApiKeyManager)
    {
        // Listen for API key dialog result
        supportFragmentManager.setFragmentResultListener(
            ApiKeyDialogFragment.REQUEST_KEY,
            this
        ) { _, bundle ->
            val key = bundle.getString(ApiKeyDialogFragment.BUNDLE_KEY_API).orEmpty()
            if (key.isNotEmpty()) {
                apiKeyManager.saveApiKey(key)
                userAPI = key
            }
        }

        // Show dialog if no key saved
        if (!apiKeyManager.hasValidApiKey()) {
            ApiKeyDialogFragment.newInstance().show(
                supportFragmentManager,
                "ApiKeyDialog"
            )

        } else {
            userAPI = apiKeyManager.getApiKey()
        }
    }

    private fun getUserLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null && userAPI != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        Toast.makeText(
                            this,
                            "Lat: $latitude, Lon: $longitude",
                            Toast.LENGTH_LONG
                        ).show()

                        // Launch coroutine to fetch weather data
                        CoroutineScope(Dispatchers.IO).launch {
                            val weatherResponse = weatherService.getCurrentWeather(
                                latitude,
                                longitude,
                                userAPI!!
                            )
                            Log.d("WeatherResponse", weatherResponse.toString())
                        }
                    } else {
                        Toast.makeText(this, "Location unavailable 😕", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Failed to get location: ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun getWeatherItems()
    {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val forecast = weatherService.getCurrentWeather(
                    latitude,
                    longitude,
                    userAPI!!
                )

                val dailySummaries = forecast.toDailySummary()

                dailySummaries.forEach {
                    Log.d("DailyForecast",
                        "${it.date} → ${it.avgTemp}°C, ${it.description}, icon: ${it.icon}"
                    )
                }
                // ToDO: pass `dailySummaries` to RecyclerView adapter for UI

                // ToDo: now sho these in text views

            } catch (e: Exception) {
                Log.e("Forecast", "Error fetching forecast", e)
            }
        }
    }

}
