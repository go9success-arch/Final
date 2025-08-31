// WellnessRepository.kt
package com.aim.lifemate.data.repositories

import android.content.Context
import com.aim.lifemate.data.models.WellnessRemedy
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WellnessRepository @Inject constructor(private val context: Context) {

    fun getRemedies(): Flow<List<WellnessRemedy>> = flow {
        try {
            val inputStream = context.resources.openRawResource(
                context.resources.getIdentifier("natural_wellness", "raw", context.packageName)
            )
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<WellnessRemedy>>() {}.type
            val remedies = Gson().fromJson<List<WellnessRemedy>>(jsonString, listType)
            emit(remedies ?: emptyList())
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    fun searchRemedies(query: String, remedies: List<WellnessRemedy>, language: String): List<WellnessRemedy> {
        if (query.isEmpty()) return remedies

        val lowercaseQuery = query.lowercase()
        return remedies.filter { remedy ->
            remedy.getPracticeName(language).lowercase().contains(lowercaseQuery) ||
                    remedy.getWellnessFocus(language).lowercase().contains(lowercaseQuery) ||
                    remedy.getRegion(language).lowercase().contains(lowercaseQuery) ||
                    remedy.tags.any { it.lowercase().contains(lowercaseQuery) } ||
                    remedy.getIngredients(language).any { it.lowercase().contains(lowercaseQuery) }
        }
    }
}