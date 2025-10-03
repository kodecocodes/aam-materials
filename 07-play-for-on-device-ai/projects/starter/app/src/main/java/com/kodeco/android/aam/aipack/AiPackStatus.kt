package com.kodeco.android.aam.aipack

sealed class AiPackStatus {
  object Unknown : AiPackStatus()
  object NotInstalled : AiPackStatus()
  object RequestConfirmation : AiPackStatus()
  data class Downloading(val progress: Int) : AiPackStatus()
  data class Installed(val location: String) : AiPackStatus()
  data class Failed(val errorCode: Int) : AiPackStatus()
}
