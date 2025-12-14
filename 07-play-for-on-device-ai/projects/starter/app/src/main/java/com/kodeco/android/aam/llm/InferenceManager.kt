/*
 * Copyright (c) 2025 Kodeco Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.kodeco.android.aam.llm

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.google.common.util.concurrent.ListenableFuture
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.LlmInferenceSession
import com.google.mediapipe.tasks.genai.llminference.LlmInferenceSession.LlmInferenceSessionOptions
import com.google.mediapipe.tasks.genai.llminference.ProgressListener
import java.io.File
import kotlin.math.max

/**
 * The `InferenceManager` class is responsible for managing the Llama model and performing
 * inference. It handles loading the model, creating an inference session, and generating responses
 * based on user prompts.
 */
class InferenceManager(context: Context, modelPath: String) {

  private val TAG = InferenceManager::class.qualifiedName

  private lateinit var llmInference: LlmInference
  private lateinit var llmInferenceSession: LlmInferenceSession

  /**
   * This function initializes the InferenceManager. It is responsible for checking if the model
   * exists, creating the LLM inference engine, and creating the LLM inference session.
   */
  init {
    if (!modelExists(context)) {
      Log.e(TAG, "Model not found at path: ${LLM_MODEL.path}")
    }
    createEngine(context, modelPath)
    createSession()
  }

  /**
   * This function creates the LLM inference engine. It is responsible for configuring the engine
   * with the correct model path, maximum number of tokens, and preferred backend.
   */
  private fun createEngine(context: Context, modelPath: String) {
    val inferenceOptions = LlmInference.LlmInferenceOptions.builder()
      .setModelPath(modelPath)
      .setMaxTokens(MAX_TOKENS)
      .setPreferredBackend(LLM_MODEL.llmInferenceBackend)
      .build()

    try {
      llmInference = LlmInference.createFromOptions(context, inferenceOptions)
    } catch (e: Exception) {
      Log.e(TAG, "Failed to load model. Error: ${e.message}", e)
    }
  }

  /**
   * This function creates the LLM inference session. It is responsible for configuring the
   * session with the appropriate temperature, top-k, and top-p values. These values control the
   * randomness and creativity of the generated response.
   */
  private fun createSession() {
    val sessionOptions = LlmInferenceSessionOptions.builder()
      .setTemperature(LLM_MODEL.temperature)
      .setTopK(LLM_MODEL.topK)
      .setTopP(LLM_MODEL.topP)
      .build()

    try {
      llmInferenceSession = LlmInferenceSession.createFromOptions(llmInference, sessionOptions)
    } catch (e: Exception) {
      Log.e(TAG, "Failed to create LlmInference session. Error: ${e.message}", e)
    }
  }

  /**
   * This function generates a response from the LLM. It takes a prompt as input and returns a
   * [ListenableFuture] that will complete with the generated response. It also takes a
   * [ProgressListener] that will be called with partial responses as they are generated. This is
   * useful for streaming the response to the UI.
   */
  fun generateResponseAsync(
    prompt: String,
    progressListener: ProgressListener<String>
  ): ListenableFuture<String> {
    llmInferenceSession.addQueryChunk(prompt)
    return llmInferenceSession.generateResponseAsync(progressListener)
  }

  /**
   * This function estimates the number of tokens remaining in the context window. It takes a
   * string as input and returns the number of tokens that can still be added to the context
   * window. If the context window is empty, it returns -1.
   *
   * @param contextWindow The string to estimate the number of tokens remaining for.
   * @return The number of tokens remaining in the context window, or -1 if the context window
   * is empty. The value is capped at 0, so it will not return a negative number.
   */
  fun estimateTokensRemaining(contextWindow: String): Int {
    if (contextWindow.isEmpty()) return -1

    val sizeOfAllMessages = llmInferenceSession.sizeInTokens(contextWindow)
    val remainingTokens = MAX_TOKENS - sizeOfAllMessages

    return max(0, remainingTokens)
  }

  /**
   * This function resets the LLM inference session. It is useful for clearing the chat history
   * and starting a new conversation. It does this by closing the current session and creating a
   * new one.
   */
  fun resetSession() {
    llmInferenceSession.close()
    createSession()
  }

  /** File Operations **/
  private fun modelPathFromUrl(context: Context): String {
    if (LLM_MODEL.url.isNotEmpty()) {
      val urlFileName = LLM_MODEL.url.toUri().lastPathSegment
      if (!urlFileName.isNullOrEmpty()) {
        return File(context.filesDir, urlFileName).absolutePath
      }
    }

    return ""
  }

  private fun modelPath(context: Context): String {
    val modelFile = File(LLM_MODEL.path)
    if (modelFile.exists()) {
      return LLM_MODEL.path
    }

    return modelPathFromUrl(context)
  }

  private fun modelExists(context: Context): Boolean {
    return File(modelPath(context)).exists()
  }

  /** File Operations **/

  companion object Companion {
    val LLM_MODEL = Model.TINYLLAMA_1_1B_CHAT_V1_0

    /** The maximum number of tokens the model can process. */
    const val MAX_TOKENS = 1024
  }
}
