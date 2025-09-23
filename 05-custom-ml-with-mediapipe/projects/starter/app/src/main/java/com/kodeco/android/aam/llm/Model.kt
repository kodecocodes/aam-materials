package com.kodeco.android.aam.llm

import com.google.mediapipe.tasks.genai.llminference.LlmInference.Backend

enum class Model(
  val path: String,
  val url: String,
  val llmInferenceBackend: Backend,
  val thinking: Boolean,
  val temperature: Float,
  val topK: Int,
  val topP: Float,
) {
  TINYLLAMA_1_1B_CHAT_V1_0(
    path = "/data/local/tmp/llm/TinyLlama-1.1B-Chat-v1.0_multi-prefill-seq_q8_ekv1280.task",
    url = "https://huggingface.co/litert-community/TinyLlama-1.1B-Chat-v1.0/resolve/main/TinyLlama-1.1B-Chat-v1.0_multi-prefill-seq_q8_ekv1280.task",
    llmInferenceBackend = Backend.CPU,
    thinking = false,
    temperature = 0.95f,
    topK = 40,
    topP = 1.0f
  ),
}
