package com.kodeco.android.aam.live

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.ai.LiveGenerativeModel
import com.google.firebase.ai.type.PublicPreviewAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

  private val _liveSessionState = MutableStateFlow<LiveSessionState>(LiveSessionState.Unknown())
  val liveSessionState = _liveSessionState.asStateFlow()

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
