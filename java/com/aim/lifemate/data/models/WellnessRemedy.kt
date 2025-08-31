// WellnessRemedy.kt
package com.aim.lifemate.data.models

import androidx.annotation.Keep

@Keep
data class WellnessRemedy(
    val id: Int,
    val wellness_focus: Map<String, String> = emptyMap(),
    val practice_name: Map<String, String> = emptyMap(),
    val ingredients: Map<String, List<String>> = emptyMap(),
    val preparation: Map<String, String> = emptyMap(),
    val suggestion: Map<String, String> = emptyMap(),
    val precautions: Map<String, String> = emptyMap(),
    val source: Map<String, String> = emptyMap(),
    val region: Map<String, String> = emptyMap(),
    val effectiveness_level: Map<String, String> = emptyMap(),
    val description: String = "", // Make sure this property exists
    val name: String = "",
    val tags: List<String> = emptyList()
) {
    fun getWellnessFocus(language: String = "en"): String {
        return wellness_focus[language] ?: wellness_focus["en"] ?: wellness_focus.values.firstOrNull() ?: ""
    }

    fun getPracticeName(language: String = "en"): String {
        return practice_name[language] ?: practice_name["en"] ?: practice_name.values.firstOrNull() ?: ""
    }

    fun getIngredients(language: String = "en"): List<String> {
        return ingredients[language] ?: ingredients["en"] ?: ingredients.values.firstOrNull() ?: emptyList()
    }

    fun getPreparation(language: String = "en"): String {
        return preparation[language] ?: preparation["en"] ?: preparation.values.firstOrNull() ?: ""
    }

    fun getSuggestion(language: String = "en"): String {
        return suggestion[language] ?: suggestion["en"] ?: suggestion.values.firstOrNull() ?: ""
    }

    fun getPrecautions(language: String = "en"): String {
        return precautions[language] ?: precautions["en"] ?: precautions.values.firstOrNull() ?: ""
    }

    fun getSource(language: String = "en"): String {
        return source[language] ?: source["en"] ?: source.values.firstOrNull() ?: ""
    }

    fun getRegion(language: String = "en"): String {
        return region[language] ?: region["en"] ?: region.values.firstOrNull() ?: ""
    }

    fun getEffectivenessLevel(language: String = "en"): String {
        return effectiveness_level[language] ?: effectiveness_level["en"] ?: effectiveness_level.values.firstOrNull() ?: ""
    }
}