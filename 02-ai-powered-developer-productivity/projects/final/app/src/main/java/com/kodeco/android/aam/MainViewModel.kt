package com.kodeco.android.aam

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel for the main screen.
 *
 * This ViewModel is responsible for providing a list of cat breeds
 * to the UI. The list of breeds is initialized when the ViewModel
 * is created.
 */
class MainViewModel : ViewModel() {

    private val _catBreeds = MutableStateFlow<List<String>>(emptyList())
    val catBreeds: StateFlow<List<String>> = _catBreeds

    init {
        _catBreeds.value = listOf(
            "Siamese",
            "Persian",
            "Maine Coon",
            "Ragdoll",
            "Bengal",
            "Abyssinian",
            "Birman",
            "Oriental Shorthair",
            "Sphynx",
            "Devon Rex",
            "Himalayan",
            "American Shorthair"
        )
    }
}