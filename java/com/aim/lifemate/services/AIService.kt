package com.aim.lifemate.services

import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import khttp.post

@Singleton
class AIService @Inject constructor() {

    companion object {
        private const val OPENAI_URL = "https://api.openai.com/v1/chat/completions"
        private const val API_KEY = "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" // Replace with actual key
    }

    suspend fun getAIResponse(prompt: String, context: String = "general"): String {
        return try {
            val messages = when (context) {
                "career" -> listOf(
                    mapOf("role" to "system", "content" to "You are a career advisor specializing in Indian job market. Provide helpful, actionable advice."),
                    mapOf("role" to "user", "content" to prompt)
                )
                "wellness" -> listOf(
                    mapOf("role" to "system", "content" to "You are a wellness expert specializing in natural remedies and traditional Indian medicine."),
                    mapOf("role" to "user", "content" to prompt)
                )
                else -> listOf(
                    mapOf("role" to "system", "content" to "You are a helpful assistant."),
                    mapOf("role" to "user", "content" to prompt)
                )
            }

            val response = post(
                url = OPENAI_URL,
                headers = mapOf(
                    "Authorization" to "Bearer $API_KEY",
                    "Content-Type" to "application/json"
                ),
                json = mapOf(
                    "model" to "gpt-3.5-turbo",
                    "messages" to messages,
                    "max_tokens" to 500,
                    "temperature" to 0.7
                )
            )

            response.jsonObject
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
        } catch (e: Exception) {
            "I apologize, but I'm currently unable to process your request. Please try again later."
        }
    }

    suspend fun getStockInsights(symbol: String): String {
        // Using Alpha Vantage API (free tier)
        return try {
            val apiKey = "your_alpha_vantage_key" // Replace with actual key
            val url = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=$symbol&apikey=$apiKey"

            val response = khttp.get(url).jsonObject
            val quote = response.getJSONObject("Global Quote")

            val price = quote.getString("05. price")
            val change = quote.getString("09. change")
            val changePercent = quote.getString("10. change percent")

            "Current Price: ₹$price | Change: $change ($changePercent)"
        } catch (e: Exception) {
            "Unable to fetch stock data for $symbol. Please check the symbol and try again."
        }
    }
}