package com.aim.lifemate.services

import android.content.Context
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceRecognitionService @Inject constructor(private val context: Context) {

    private val _isListening = mutableStateOf(false)
    val isListening get() = _isListening.value

    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    private var speechRecognizer: SpeechRecognizer? = null

    fun startListening(language: String = "en-IN") {
        if (SpeechRecognizer.isRecognitionAvailable(context).not()) {
            _error.value = "Voice recognition not available"
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(createRecognitionListener())
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        _isListening.value = true
        _error.value = ""
        speechRecognizer?.startListening(intent)
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _isListening.value = false
    }

    fun destroy() {
        speechRecognizer?.destroy()
        _isListening.value = false
    }

    private fun createRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: android.os.Bundle?) {
                _isListening.value = true
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                _isListening.value = false
            }

            override fun onError(error: Int) {
                _isListening.value = false
                _error.value = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permissions error"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No match found"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
                    SpeechRecognizer.ERROR_SERVER -> "Server error"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout"
                    else -> "Unknown error: $error"
                }
            }

            override fun onResults(results: android.os.Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                _recognizedText.value = matches?.firstOrNull() ?: ""
                _isListening.value = false
            }

            override fun onPartialResults(partialResults: android.os.Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                _recognizedText.value = matches?.firstOrNull() ?: ""
            }

            override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
        }
    }

    fun getAvailableLanguages(): List<String> {
        return listOf(
            "en-IN", // English (India)
            "hi-IN", // Hindi
            "ta-IN", // Tamil
            "te-IN", // Telugu
            "bn-IN", // Bengali
            "mr-IN", // Marathi
            "gu-IN", // Gujarati
            "kn-IN", // Kannada
            "ml-IN", // Malayalam
            "pa-IN", // Punjabi
            "ur-IN"  // Urdu
        )
    }

    fun getLanguageName(code: String): String {
        return when (code) {
            "en-IN" -> "English"
            "hi-IN" -> "Hindi"
            "ta-IN" -> "Tamil"
            "te-IN" -> "Telugu"
            "bn-IN" -> "Bengali"
            "mr-IN" -> "Marathi"
            "gu-IN" -> "Gujarati"
            "kn-IN" -> "Kannada"
            "ml-IN" -> "Malayalam"
            "pa-IN" -> "Punjabi"
            "ur-IN" -> "Urdu"
            else -> "Unknown"
        }
    }
}