package com.example.utkarsh.ui.chat

import android.app.Application
import android.media.MediaRecorder
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> = _messages

    val isRecording = mutableStateOf(false)
    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null

    // Gemini API Key integrated
    private val generativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = "AIzaSyBbALG4NFWyqPvzR7RpmBBJsli7bf4S3LY"
    )

    private val chat = generativeModel.startChat(
        history = listOf(
            content(role = "user") { text("You are a helpful and empathetic AI psychologist named Utkarsh. You support users with their mental wellness. You can speak English and various Indian languages.") },
            content(role = "model") { text("Understood. I am Utkarsh, your AI psychologist. How can I help you today?") }
        )
    )

    init {
        _messages.add(ChatMessage("Hello, I'm Utkarsh. How are you feeling today?", false))
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMessage = ChatMessage(text, true)
        _messages.add(userMessage)

        viewModelScope.launch {
            try {
                val response = chat.sendMessage(text)
                response.text?.let {
                    _messages.add(ChatMessage(it, false))
                }
            } catch (e: Exception) {
                _messages.add(
                    ChatMessage(
                        "Sorry, I'm having trouble connecting. ${e.localizedMessage}",
                        false
                    )
                )
            }
        }
    }

    fun startRecording() {
        try {
            val context = getApplication<Application>().applicationContext
            audioFile = File(context.cacheDir, "recording_${System.currentTimeMillis()}.mp3")

            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFile?.absolutePath)
                prepare()
                start()
            }
            isRecording.value = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopAndSaveRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording.value = false

            uploadRecording()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun uploadRecording() {
        val user = Firebase.auth.currentUser ?: return
        val file = audioFile ?: return

        val storageRef = Firebase.storage.reference.child("recordings/${user.uid}/${file.name}")
        val transcriptRef = Firebase.firestore.collection("sessions").document()

        viewModelScope.launch {
            // Upload Audio
            storageRef.putFile(android.net.Uri.fromFile(file))
                .addOnSuccessListener {
                    // Save Transcript after audio upload success
                    val transcriptData = hashMapOf(
                        "userId" to user.uid,
                        "timestamp" to System.currentTimeMillis(),
                        "audioUrl" to it.metadata?.path,
                        "messages" to _messages.map {
                            mapOf("text" to it.text, "isUser" to it.isUser, "time" to it.timestamp)
                        }
                    )
                    transcriptRef.set(transcriptData)
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (isRecording.value) {
            stopAndSaveRecording()
        }
    }
}
