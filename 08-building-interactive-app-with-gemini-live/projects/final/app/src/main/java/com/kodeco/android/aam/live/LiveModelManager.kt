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
package com.kodeco.android.aam.live

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.ai.LiveGenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.FunctionCallPart
import com.google.firebase.ai.type.FunctionDeclaration
import com.google.firebase.ai.type.FunctionResponsePart
import com.google.firebase.ai.type.GenerativeBackend.Companion.googleAI
import com.google.firebase.ai.type.LiveSession
import com.google.firebase.ai.type.PublicPreviewAPI
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.SpeechConfig
import com.google.firebase.ai.type.Tool
import com.google.firebase.ai.type.Voice
import com.google.firebase.ai.type.liveGenerationConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

/**
 * Manages the Gemini Live model and its sessions.
 *
 * This class handles the initialization of the Gemini Live model, manages audio permissions,
 * and controls the lifecycle of a live session, including starting, stopping, and handling
 * function calls.
 *
 * @param context The application context.
 * @param coroutineScope The coroutine scope for launching background tasks.
 */
@OptIn(PublicPreviewAPI::class)
class LiveModelManager(
  private val context: Context,
  private val coroutineScope: CoroutineScope
) {
  val TAG = "LiveModelManager"

  private lateinit var liveModel: LiveGenerativeModel
  private var session: LiveSession? = null

  private val _liveSessionState = MutableStateFlow<LiveSessionState>(LiveSessionState.Unknown())
  val liveSessionState = _liveSessionState.asStateFlow()

  /**
   * Initializes the Gemini Live model.
   *
   * This function sets up the model configuration, requests audio permissions,
   * and prepares the model for use.
   *
   * @param activity The current activity, used for requesting permissions.
   */
  fun initializeGeminiLive(activity: Activity) {
    requestAudioPermissionIfNeeded(activity)

    coroutineScope.launch {
      try {
        val liveGenerationConfig = liveGenerationConfig {
          speechConfig = SpeechConfig(voice = Voice("FENRIR"))
          responseModality = ResponseModality.AUDIO
        }

        liveModel = Firebase.ai(backend = googleAI()).liveModel(
          modelName = "gemini-live-2.5-flash-preview",
          generationConfig = liveGenerationConfig,
          tools = listOf(functionHandlerTool),
        )

        _liveSessionState.value = LiveSessionState.Ready()
      } catch (e: Exception) {
        _liveSessionState.value = LiveSessionState.Error(message = e.localizedMessage)
      }
    }
  }

  /**
   * Starts a new Gemini Live session from a text prompt.
   *
   * @param catBreed The cat breed to ask the model about.
   */
  @RequiresPermission(Manifest.permission.RECORD_AUDIO)
  fun startSessionFromText(catBreed: String) {
    val text = "Tell me about $catBreed cats."

    coroutineScope.launch(Dispatchers.IO) {
      try {
        // Start the conversation
        session = liveModel.connect()
        session?.send(text)
        session?.startAudioConversation(::functionCallHandler)
        // Update State
        _liveSessionState.value = LiveSessionState.Running()
      } catch (e: Exception) {
        _liveSessionState.value = LiveSessionState.Error(message = e.localizedMessage)
      }
    }
  }

  /**
   * Stops the current Gemini Live session.
   */
  fun stopSession() {
    session?.apply {
      stopAudioConversation()
      stopReceiving()
    }
    _liveSessionState.value = LiveSessionState.Ready()
  }

  /**
   * Opens Google Images to show pictures of a specific cat breed.
   *
   * @param catBreed The cat breed to search for.
   */
  fun showPicture(catBreed: String) {
    coroutineScope.launch(Dispatchers.Default) {
      val query = Uri.encode("$catBreed cat pictures")
      val url = "https://www.google.com/search?q=$query&tbm=isch"

      val intent = Intent(Intent.ACTION_VIEW)
      intent.data = Uri.parse(url)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

      try {
        context.startActivity(intent)
      } catch (e: Exception) {
        Log.e(TAG, "Error opening Google Images", e)
      }
    }
  }

  /**
   * The function declaration for the `showPicture` tool.
   */
  val showPictureFunctionDeclaration = FunctionDeclaration(
    name = "showPicture",
    description = "Function to show picture of cat breed",
    parameters = mapOf(
      "catBreed" to Schema.string(
        description = "A short string describing the cat breed to show picture"
      )
    )
  )

  /**
   * The tool that provides the `showPicture` function to the model.
   */
  val functionHandlerTool = Tool.functionDeclarations(listOf(showPictureFunctionDeclaration))

  /**
   * Handles function calls from the Gemini Live model.
   *
   * @param functionCall The function call to handle.
   * @return A function response part.
   */
  fun functionCallHandler(functionCall: FunctionCallPart): FunctionResponsePart {
    return when (functionCall.name) {
      "showPicture" -> {
        val catBreed = functionCall.args["catBreed"]!!.jsonPrimitive.content
        showPicture(catBreed = catBreed)
        val response = JsonObject(
          mapOf(
            "success" to JsonPrimitive(true),
            "message" to JsonPrimitive("Showing pictures of $catBreed")
          )
        )
        FunctionResponsePart(functionCall.name, response)
      }

      else -> {
        val response = JsonObject(
          mapOf(
            "error" to JsonPrimitive("Unknown function: ${functionCall.name}")
          )
        )
        FunctionResponsePart(functionCall.name, response)
      }
    }
  }

  /**
   * Requests audio permission from the user if it has not already been granted.
   *
   * @param activity The current activity, used to request the permission.
   */
  private fun requestAudioPermissionIfNeeded(activity: Activity) {
    if (ContextCompat.checkSelfPermission(
        activity,
        Manifest.permission.RECORD_AUDIO,
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
    }
  }
}
