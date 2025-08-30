package com.kodeco.android.aam

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel

class MainViewModel(application: Application) : AndroidViewModel(application) {

  // State to hold the recognized images
  val pageUris = mutableStateListOf<Uri>()

  // Prepare Document Scanning Client
  fun prepareScanner() {

  }

  // Extract the pages from the result
  fun extractPages() {

  }

  // Extract text from image
  fun getTextFromImage(image: Uri, onCompleted: (String?) -> Unit) {

  }
}