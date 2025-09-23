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
