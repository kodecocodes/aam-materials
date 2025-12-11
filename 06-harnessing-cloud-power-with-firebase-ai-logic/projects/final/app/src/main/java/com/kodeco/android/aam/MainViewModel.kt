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
package com.kodeco.android.aam

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.ImagenAspectRatio
import com.google.firebase.ai.type.ImagenGenerationConfig
import com.google.firebase.ai.type.ImagenImageFormat
import com.google.firebase.ai.type.PublicPreviewAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@OptIn(PublicPreviewAPI::class)
class MainViewModel : ViewModel() {

  private val _catBreeds = MutableStateFlow<List<String>>(emptyList())
  val catBreeds: StateFlow<List<String>> = _catBreeds.asStateFlow()

  private val _isLoading = MutableStateFlow(false)
  val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  private val _catImage = MutableStateFlow<Bitmap?>(null)
  val catImage: StateFlow<Bitmap?> = _catImage.asStateFlow()

  private val _catDescription = MutableStateFlow("")
  val catDescription: StateFlow<String> = _catDescription.asStateFlow()

  init {
    _catBreeds.value = listOf(
      "Siamese", "Persian", "Maine Coon", "Ragdoll", "Bengal",
      "Abyssinian", "Birman", "Oriental Shorthair", "Sphinx",
      "Devon Rex", "Himalayan", "American Shorthair", "Scottish Fold",
      "Burmese", "Russian Blue", "Manx", "Norwegian Forest Cat",
      "Siberian", "Cornish Rex", "Tonkinese", "Exotic Shorthair",
      "Chartreux", "Egyptian Mau", "Selkirk Rex", "Turkish Angora",
      "Balinese", "Japanese Bobtail", "Korat", "Ocicat"
    )
  }

  // Initialize Firebase AI Logic
  val firebaseAI = Firebase.ai(
    backend = GenerativeBackend.googleAI(),
    useLimitedUseAppCheckTokens = true
  )

  // Image Generation
  val imagenConfig = ImagenGenerationConfig(
    numberOfImages = 1,
    aspectRatio = ImagenAspectRatio.LANDSCAPE_16x9,
    imageFormat = ImagenImageFormat.png(),
  )

  val imagenModel = firebaseAI.imagenModel(
    modelName = "imagen-4.0-fast-generate-001",
    generationConfig = imagenConfig,
  )

  fun generateCatImage(breed: String) {
    val prompt = "A cat of $breed breed."

    viewModelScope.launch(Dispatchers.IO) {
      _catImage.value = null

      val response = imagenModel.generateImages(prompt)

      val image = response.images.first()
      _catImage.value = image.asBitmap()
    }
  }

  // Text Generation
  val genAiModel = firebaseAI.generativeModel(modelName = "gemini-2.5-flash-lite")

  fun describeCat(breed: String) {
    val prompt = "Describe the $breed cat."

    viewModelScope.launch(Dispatchers.IO) {
      _isLoading.value = true
      _catDescription.value = "Generating details for $breed cat..."

      val response = genAiModel.generateContent(prompt)
      val responseText = response.text

      _catDescription.value = responseText ?: "Description not available."
      _isLoading.value = false
    }
  }
}
