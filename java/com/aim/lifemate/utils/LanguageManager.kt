// LanguageManager.kt
package com.aim.lifemate.utils

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageManager @Inject constructor(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val KEY_LANGUAGE = "selected_language"
    }

    val supportedLanguages = listOf("en", "es", "fr", "de", "hi", "zh", "ar")

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getDisplayLanguage(languageCode: String): String {
        return when (languageCode) {
            "en" -> "English"
            "es" -> "Español"
            "fr" -> "Français"
            "de" -> "Deutsch"
            "hi" -> "हिंदी"
            "zh" -> "中文"
            "ar" -> "العربية"
            else -> languageCode
        }
    }

    fun getCurrentLanguage(): String {
        return prefs.getString(KEY_LANGUAGE, "en") ?: "en"
    }

    fun setLanguage(languageCode: String) {
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }
}