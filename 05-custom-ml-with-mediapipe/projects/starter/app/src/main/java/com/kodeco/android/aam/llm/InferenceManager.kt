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
package com.kodeco.android.aam.llm

import android.content.Context
import androidx.core.net.toUri
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.LlmInferenceSession
import java.io.File

/**
 * The `InferenceManager` class is responsible for managing the Llama model and performing
 * inference. It handles loading the model, creating an inference session, and generating responses
 * based on user prompts.
 */
class InferenceManager(context: Context) {

  private val TAG = InferenceManager::class.qualifiedName

  private lateinit var llmInference: LlmInference
  private lateinit var llmInferenceSession: LlmInferenceSession

  /**
   * This function initializes the InferenceManager. It is responsible for checking if the model
   * exists, creating the LLM inference engine, and creating the LLM inference session.
   */
  init {
    if (!modelExists(context)) {
      throw IllegalArgumentException("Model not found at path: ${LLM_MODEL.path}")
    }
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
