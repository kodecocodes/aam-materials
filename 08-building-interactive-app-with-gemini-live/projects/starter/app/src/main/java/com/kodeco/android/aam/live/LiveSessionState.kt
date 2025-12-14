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
 * distribute, sublicense, create a derivative work, and/or sell
 * copies of the Software in any work that is designed, intended, or marketed for pedagogical or
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
