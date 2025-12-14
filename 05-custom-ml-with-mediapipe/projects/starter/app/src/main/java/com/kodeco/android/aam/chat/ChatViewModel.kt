/*
Copyright (c) 2025 Kodeco Inc.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
distribute, sublicense, create a derivative work, and/or sell copies of the
Software in any work that is designed, intended, or marketed for pedagogical or
instructional purposes related to programming, coding, application development,
or information technology.  Permission for such use, copying, modification,
merger, publication, distribution, sublicensing, creation of derivative works,
or sale is expressly withheld.

This project and source code may use libraries or frameworks that are
released under various Open-Source licenses. Use of those libraries and
frameworks are governed by their own individual licenses.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
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
import kotlinx.coroutines.launch

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
    }
  }

}
