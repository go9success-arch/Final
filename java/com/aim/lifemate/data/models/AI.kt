package com.aim.lifemate.data.models

data class CareerAdvice(
    val id: String = "",
    val userId: String = "",
    val query: String = "",
    val response: String = "",
    val category: String = "",
    val language: String = "en",
    val timestamp: Long = System.currentTimeMillis(),
    val rating: Int = 0,
    val useful: Boolean = false,
    val generatedBy: String = "AI",
    val model: String = "GPT-3.5",
    val tokensUsed: Int = 0,
    val cost: Double = 0.0
)

data class StockInsight(
    val id: String = "",
    val symbol: String = "",
    val name: String = "",
    val currentPrice: Double = 0.0,
    val previousClose: Double = 0.0,
    val change: Double = 0.0,
    val changePercent: Double = 0.0,
    val marketCap: String = "",
    val volume: String = "",
    val insight: String = "",
    val confidence: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val source: String = "",
    val isBullish: Boolean = true,
    val targetPrice: Double = 0.0,
    val stopLoss: Double = 0.0
)

data class VoiceCommand(
    val id: String = "",
    val userId: String = "",
    val command: String = "",
    val language: String = "en",
    val processedText: String = "",
    val action: String = "",
    val parameters: Map<String, String> = emptyMap(),
    val success: Boolean = true,
    val timestamp: Long = System.currentTimeMillis(),
    val responseTime: Long = 0,
    val confidence: Double = 0.0
)