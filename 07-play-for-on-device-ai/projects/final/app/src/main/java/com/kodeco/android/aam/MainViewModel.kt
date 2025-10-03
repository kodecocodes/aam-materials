package com.kodeco.android.aam

import android.app.Application
import android.util.Log
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
import java.io.File

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
    aiPackManager.registerListener(this)
  }

  /**
   * Checks the status of a given AI pack.
   * If the pack is already installed, it updates the status immediately.
   * Otherwise, it queries the [AiPackManager] for the current state.
   *
   * @param packName The name of the AI pack to check.
   */
  fun checkPackStatus(packName: String) {
    val packLocation = aiPackManager.getPackLocation(packName)
    if (packLocation != null) {
      _aiPackStatus.value = AiPackStatus.Installed(packLocation.toString())
      return
    }

    aiPackManager.getPackStates(listOf(packName))
      .addOnSuccessListener { states ->
        val state = states.packStates()[packName]
        _aiPackStatus.value = mapAiPackStatus(state = state)
      }.addOnFailureListener { e ->
        _aiPackStatus.value = mapAiPackStatus(state = null)
      }
  }

  /**
   * Initiates a request to fetch the AI packs defined in [AI_PACKS].
   * This may trigger a download or a user confirmation dialog.
   */
  fun fetchAiPacks() {
    aiPackManager.fetch(AI_PACKS)
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
    aiPackManager.cancel(AI_PACKS)
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
    val aiPackLocation = aiPackManager.getPackLocation(packName)
    val assetsFolderPath = aiPackLocation?.assetsPath()
    val assetFile = File(assetsFolderPath, assetName)
    Log.d(TAG, "Asset path: ${assetFile.absolutePath}")

    return if (assetFile.exists()) assetFile.absolutePath else null
  }

  /**
   * Callback method from [AiPackStateUpdateListener].
   * This is triggered whenever the state of a registered AI pack changes.
   *
   * @param state The updated [AiPackState].
   */
  override fun onStateUpdate(state: AiPackState) {
    Log.d(TAG, "onStateUpdate: $state")
    _aiPackStatus.value = mapAiPackStatus(state)
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
