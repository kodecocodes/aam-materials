package com.kodeco.android.aam.live

/**
 * Represents the various states of the Gemini Live session.
 *
 * This sealed interface is used to manage the UI and logic based on the current
 * status of the `LiveGenerativeModel`.
 */
sealed interface LiveSessionState {
  /**
   * The initial state before Gemini Live is initialized.
   *
   * @param message A descriptive message for the unknown state.
   */
  data class Unknown(val message: String = "UNKNOWN: Gemini Live Not Initialized") : LiveSessionState

  /**
   * The state when Gemini Live is initialized and ready to receive requests.
   *
   * @param message A descriptive message for the ready state.
   */
  data class Ready(val message: String = "READY: Ask Gemini Live") : LiveSessionState

  /**
   * The state when Gemini Live is actively processing a request and providing an audio response.
   *
   * @param message A descriptive message for the running state.
   */
  data class Running(val message: String = "RUNNING: Gemini Live Speaking...") : LiveSessionState

  /**
   * The state when an error has occurred during the Gemini Live session.
   *
   * @param message A descriptive message for the error state.
   */
  data class Error(val message: String = "ERROR: Failed to initiate lGemini Live") : LiveSessionState
}
