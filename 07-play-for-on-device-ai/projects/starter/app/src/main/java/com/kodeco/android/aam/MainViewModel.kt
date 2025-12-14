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

import android.app.Application
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.AndroidViewModel
import com.google.android.play.core.aipacks.AiPackManager
import com.google.android.play.core.aipacks.AiPackManagerFactory
import com.google.android.play.core.aipacks.AiPackState
import com.google.android.play.core.aipacks.AiPackStateUpdateListener
import com.google.android.play.core.assetpacks.model.AssetPackStatus
import com.kodeco.android.aam.MainViewModel.Companion.AI_PACKS
import com.kodeco.android.aam.aipack.AiPackStatus
import com.kodeco.android.aam.llm.Model
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * The ViewModel for the main screen.
 *
 * This ViewModel is responsible for managing the state of AI packs,
 * including checking their status, fetching them, and handling user confirmations.
 * It interacts with the [AiPackManager] to perform these operations and exposes the
 * pack status as a [StateFlow] for the UI to observe.
 *
 * @param application The application instance, used to get the context for [AiPackManagerFactory].
 */
class MainViewModel(application: Application) : AndroidViewModel(application),
  AiPackStateUpdateListener {

  /** Manages AI pack operations like fetching, checking status, and listening for updates. */
  private val aiPackManager: AiPackManager = AiPackManagerFactory.getInstance(application)

  /**
   * Private mutable state flow that holds the current status of the AI pack.
   * The UI observes the public, immutable [aiPackStatus].
   */
  private val _aiPackStatus = MutableStateFlow<AiPackStatus>(AiPackStatus.Unknown)

  /** Public, immutable [StateFlow] representing the current status of the AI pack. */
  val aiPackStatus: StateFlow<AiPackStatus> = _aiPackStatus.asStateFlow()

  init {
    // TODO: Register for updates
  }

  /**
   * Checks the status of a given AI pack.
   * If the pack is already installed, it updates the status immediately.
   * Otherwise, it queries the [AiPackManager] for the current state.
   *
   * @param packName The name of the AI pack to check.
   */
  fun checkPackStatus(packName: String) {
    // TODO: If the pack is already installed, update the status
  }

  /**
   * Initiates a request to fetch the AI packs defined in [AI_PACKS].
   * This may trigger a download or a user confirmation dialog.
   */
  fun fetchAiPacks() {
    // TODO: Initiate the fetch request
  }

  /**
   * Shows a confirmation dialog to the user for downloading a large pack.
   * This is typically used when the pack status is [AssetPackStatus.REQUIRES_USER_CONFIRMATION].
   *
   * @param activityResultLauncher The launcher to start the confirmation dialog's intent.
   */
  fun showConfirmationDialog(activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>) {
    aiPackManager.showConfirmationDialog(activityResultLauncher)
  }

  /**
   * Cancels any ongoing download requests for the AI packs.
   */
  fun cancelRequests() {
    // TODO: Cancel any ongoing requests
  }

  /**
   * Maps the [AiPackState] from the Play Core library to the app-specific [AiPackStatus].
   * This simplifies the state management for the UI.
   *
   * @param state The [AiPackState] from the [AiPackManager], which can be null.
   * @return The corresponding [AiPackStatus] for the UI.
   */
  private fun mapAiPackStatus(state: AiPackState?): AiPackStatus {
    return when (state?.status()) {
      AssetPackStatus.NOT_INSTALLED -> {
        AiPackStatus.NotInstalled
      }

      AssetPackStatus.WAITING_FOR_WIFI,
      AssetPackStatus.REQUIRES_USER_CONFIRMATION -> {
        AiPackStatus.RequestConfirmation
      }

      AssetPackStatus.PENDING,
      AssetPackStatus.DOWNLOADING,
      AssetPackStatus.TRANSFERRING -> {
        AiPackStatus.Downloading(state.transferProgressPercentage())
      }

      AssetPackStatus.COMPLETED -> {
        val assetLocation = getAssetFromAiPack(
          packName = AI_PACK_NAME,
          assetName = Model.TINYLLAMA_1_1B_CHAT_V1_0.modelName
        )
        if (assetLocation == null) {
          AiPackStatus.NotInstalled
        } else {
          AiPackStatus.Installed(location = assetLocation)
        }
      }

      AssetPackStatus.CANCELED,
      AssetPackStatus.FAILED -> {
        AiPackStatus.Failed(errorCode = state.errorCode())
      }

      else -> {
        AiPackStatus.Unknown
      }
    }
  }

  /**
   * Retrieves the absolute path of a specific asset from a downloaded AI pack.
   * It constructs the file path based on the pack's asset directory and the asset's name,
   * then verifies if the file exists before returning its path.
   *
   * @param packName The name of the AI pack.
   * @param assetName The name of the asset file within the pack.
   * @return The absolute path of the asset file as a [String] if it exists, otherwise `null`.
   */
  private fun getAssetFromAiPack(packName: String, assetName: String): String? {
    // TODO: Retrieve the asset from the AI pack
    return null
  }

  /**
   * Callback method from [AiPackStateUpdateListener].
   * This is triggered whenever the state of a registered AI pack changes.
   *
   * @param state The updated [AiPackState].
   */
  override fun onStateUpdate(state: AiPackState) {
    // TODO: Update the status based on the received state
  }

  companion object {
    private const val TAG = "MainViewModel"

    /** The name of the on-demand AI pack used in this app. */
    const val AI_PACK_NAME = "onDemandAiPack"

    /**
     * A list containing the names of all AI packs this ViewModel manages.
     */
    val AI_PACKS = listOf(AI_PACK_NAME)
  }
}
