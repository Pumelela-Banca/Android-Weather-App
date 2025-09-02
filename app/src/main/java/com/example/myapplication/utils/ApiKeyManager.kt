package com.example.myapplication.utils
import android.content.Context
import android.content.SharedPreferences

class ApiKeyManager(context: Context) {

    companion object {
        private const val PREFS_NAME = "ApiKeyPrefs"
        private const val API_KEY_KEY = "apiKey"
        private const val KEY_LAST_USED = "last_used"
        private const val KEY_LAST_USED_TIME = 4 * 24 * 60 * 60 * 1000L
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Save the API key and set last used timestamp

    fun saveApiKey(apiKey: String) {
        prefs.edit().apply() {
            putString(API_KEY_KEY, apiKey)
            putLong(KEY_LAST_USED, System.currentTimeMillis())
            apply()
        }
    }

    /**
    * Get the API key and check if it's still valid, nullifying it if expired or missing
    */

    fun getApiKey(): String? {
        val apiKey = prefs.getString(API_KEY_KEY, null)
        val lastUsed = prefs.getLong(KEY_LAST_USED, 0L)

        return if (apiKey != null && System.currentTimeMillis() - lastUsed < KEY_LAST_USED_TIME) {
            // Update last used timestamp
            prefs.edit().putLong(KEY_LAST_USED, System.currentTimeMillis()).apply()
            apiKey
        } else {
            clearApiKey()
            null
        }
    }

    /**
     * Clear the API key and last used timestamp
     */
    fun clearApiKey() {
        prefs.edit().remove(API_KEY_KEY).apply()
    }

    /**
     * Check if the API key is set
     */

    fun hasValidApiKey(): Boolean = getApiKey() != null
}