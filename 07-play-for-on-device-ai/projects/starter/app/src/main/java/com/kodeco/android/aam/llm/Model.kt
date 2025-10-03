package com.kodeco.android.aam.llm

import com.google.mediapipe.tasks.genai.llminference.LlmInference.Backend

/**
 * Enum class representing the available LLM models.
 * Each model is defined with its properties required for initialization and inference.
 *
 * @property modelName The name of the model file.
 * @property path The local path where the model is stored on the device.
 * @property url The URL from where the model can be downloaded.
 * @property llmInferenceBackend The backend to use for inference (CPU or GPU).
 * @property thinking Indicates if the model should show a "thinking" animation.
 */
enum class Model(
  val modelName: String,
  val path: String,
  val url: String,
  val llmInferenceBackend: Backend,
  val thinking: Boolean,
  val temperature: Float,
  val topK: Int,
  val topP: Float,
) {
  TINYLLAMA_1_1B_CHAT_V1_0(
    modelName = "TinyLlama-1.1B-Chat-v1.0_multi-prefill-seq_q8_ekv1280.task",
    path = "/data/local/tmp/llm/TinyLlama-1.1B-Chat-v1.0_multi-prefill-seq_q8_ekv1280.task",
    url = "https://huggingface.co/litert-community/TinyLlama-1.1B-Chat-v1.0/resolve/main/TinyLlama-1.1B-Chat-v1.0_multi-prefill-seq_q8_ekv1280.task",
    llmInferenceBackend = Backend.CPU,
    thinking = false,
    temperature = 0.95f,
    topK = 40,
    topP = 1.0f
  ),
}
