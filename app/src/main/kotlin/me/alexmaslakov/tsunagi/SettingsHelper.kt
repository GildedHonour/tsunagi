package me.alexmaslakov.tsunagi

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class SettingsHelper(private val context: Context) {
    private var prefs: SharedPreferences? = null

    private fun sharedPreferences(): SharedPreferences {
        if (prefs == null) {
            prefs = PreferenceManager.getDefaultSharedPreferences(context)
        }

        return prefs!!
    }

    fun save(name: String, value: Boolean) {
        sharedPreferences().edit().putBoolean(name, value).apply()
    }

    fun save(name: String, value: String) {
        sharedPreferences().edit().putString(name, value).apply()
    }

    fun getBoolean(name: String): Boolean {
        return when (name) {
            KEY_MOBILE_DATA -> sharedPreferences().getBoolean(KEY_MOBILE_DATA, true)
            KEY_ALERT_SHOWN -> sharedPreferences().getBoolean(KEY_ALERT_SHOWN, false)
            KEY_UPDATE_SHOWN -> sharedPreferences().getBoolean(KEY_UPDATE_SHOWN, true)
            KEY_DATABASE_CREATED -> sharedPreferences().getBoolean(KEY_DATABASE_CREATED, true)
            else -> false
        }
    }

    fun getString(name: String): String {
        return when (name) {
            KEY_NOTIFICATION_SOUND -> sharedPreferences().getString(KEY_NOTIFICATION_SOUND,
                    "android.resource://" + context.packageName + "/" + R.raw.zoop)

            KEY_LAST_REFRESHED -> sharedPreferences().getString(KEY_LAST_REFRESHED, "")
            else -> ""
        }
    }

    companion object {
        const val KEY_MOBILE_DATA = "KEY_MOBILE_DATA"
        const val KEY_NOTIFICATION_SOUND = "KEY_NOTIFICATION_SOUND"
        const val KEY_LAST_REFRESHED = "KEY_LAST_REFRESHED"
        const val KEY_ALERT_SHOWN = "KEY_ALERT_SHOWN_2"
        const val KEY_UPDATE_SHOWN = "KEY_UPDATE_SHOWN_2"
        const val KEY_DATABASE_CREATED = "KEY_DATABASE_CREATED"
    }
}