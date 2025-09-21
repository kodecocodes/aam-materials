package com.kodeco.android.aam.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kodeco.android.aam.llm.InferenceManager
import com.kodeco.android.aam.llm.InferenceManager.Companion.LLM_MODEL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max

class ChatViewModel(application: Application) : AndroidViewModel(application) {

  private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(
    UiState(LLM_MODEL.thinking)
  )
  val uiState: StateFlow<UiState> = _uiState.asStateFlow()

  private val _tokensRemaining = MutableStateFlow(-1)
  val tokensRemaining: StateFlow<Int> = _tokensRemaining.asStateFlow()

  private val _isInputEnabled: MutableStateFlow<Boolean> = MutableStateFlow(true)
  val isInputEnabled: StateFlow<Boolean> = _isInputEnabled.asStateFlow()

  private lateinit var inferenceManager: InferenceManager

  init {
    viewModelScope.launch(Dispatchers.IO) {
      inferenceManager = InferenceManager(context = application)
    }
  }

  fun sendMessage(userMessage: String) {
    viewModelScope.launch(Dispatchers.IO) {
      _uiState.value.addMessage(userMessage, USER_PREFIX)
      _uiState.value.createLoadingMessage()
      _isInputEnabled.value = false

      try {
        inferenceManager.generateResponseAsync(prompt = userMessage) { progressiveResult, done ->
          _uiState.value.appendMessage(text = progressiveResult)
          _tokensRemaining.update { max(0, it - 1) }
          _isInputEnabled.value = done
        }
      } catch (e: Exception) {
        _uiState.value.addMessage(e.localizedMessage ?: "Error", MODEL_PREFIX)
      }
    }
  }

  fun calculateRemainingTokens(prompt: String) {
    val contextWindow = uiState.value.messages.joinToString { it.rawMessage } + prompt
    val remainingTokens = inferenceManager.estimateTokensRemaining(contextWindow = contextWindow)
    _tokensRemaining.value = remainingTokens
  }

  fun resetChat() {
    uiState.value.clearMessages()
    calculateRemainingTokens(prompt = "")
    inferenceManager.resetSession()
  }

}
