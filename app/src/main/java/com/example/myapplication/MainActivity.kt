package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import com.example.myapplication.ApiKeyDialogFragment
import com.example.myapplication.domain.WeatherService2
import com.example.myapplication.utils.ApiKeyManager
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private var userAPI: String? = null

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
    val apiKeyManager = ApiKeyManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userAPI = apiKeyManager.getApiKey()

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
}
