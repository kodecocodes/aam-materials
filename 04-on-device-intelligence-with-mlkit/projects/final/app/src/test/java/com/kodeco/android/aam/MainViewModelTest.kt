package com.kodeco.android.aam

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for the [MainViewModel].
 * This class contains tests to verify the functionality of the [MainViewModel],
 * specifically focusing on the initial state of the `catBreeds` LiveData.
 */
class MainViewModelTest {

  private lateinit var viewModel: MainViewModel

  /**
   * Sets up the test environment before each test.
   * Initializes a new instance of [MainViewModel].
   */
  @Before
  fun setUp() {
    viewModel = MainViewModel()
  }

  /**
   * Tests if the `catBreeds` LiveData is initialized with the correct list of cat breeds.
   * It compares the actual list of breeds from the ViewModel with an expected list.
   * This ensures that the initial data for cat breeds is loaded as expected.
   */
  @Test
  fun `catBreeds initializes with correct list of breeds`() {
    val expectedBreeds = listOf(
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
    assertEquals(expectedBreeds, viewModel.catBreeds.value)
  }

  /**
   * Tests if the initial list of `catBreeds` is not empty.
   * This is a basic sanity check to ensure that the ViewModel initializes
   * `catBreeds` with some data.
   */
  @Test
  fun `catBreeds initial list is not empty`() {
    assert(viewModel.catBreeds.value.isNotEmpty())
  }

  /**
   * Tests if the size of the initial list of `catBreeds` is correct.
   * It verifies that the number of breeds loaded initially matches the expected count.
   * In this case, it checks if there are 12 cat breeds loaded.
   */
  @Test
  fun `catBreeds initial list size is correct`() {
    assertEquals(12, viewModel.catBreeds.value.size)
  }
}
