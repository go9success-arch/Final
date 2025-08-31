package com.aim.lifemate.services

import android.content.Context
import com.aim.lifemate.data.models.WellnessRemedy
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JsonParserService @Inject constructor(
    private val context: Context
) {
    private val gson = Gson()

    suspend fun loadWellnessRemedies(): List<WellnessRemedy> = withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream = context.resources.openRawResource(
                context.resources.getIdentifier("natural_wellness", "raw", context.packageName)
            )
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<WellnessRemedy>>() {}.type
            gson.fromJson<List<WellnessRemedy>>(jsonString, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getWellnessRemedyById(id: Int): WellnessRemedy? = withContext(Dispatchers.IO) {
        loadWellnessRemedies().find { it.id == id }
    }

    suspend fun searchWellnessRemedies(query: String, language: String = "en"): List<WellnessRemedy> {
        val remedies = loadWellnessRemedies()
        if (query.isBlank()) return remedies

        val lowerQuery = query.lowercase()
        return remedies.filter { remedy ->
            remedy.getPracticeName(language).contains(lowerQuery, ignoreCase = true) ||
                    remedy.getWellnessFocus(language).contains(lowerQuery, ignoreCase = true) ||
                    remedy.getIngredients(language).any { it.contains(lowerQuery, ignoreCase = true) } ||
                    remedy.getRegion(language).contains(lowerQuery, ignoreCase = true) ||
                    remedy.tags.any { it.contains(lowerQuery, ignoreCase = true) } ||
                    remedy.getSource(language).contains(lowerQuery, ignoreCase = true)
        }
    }
}