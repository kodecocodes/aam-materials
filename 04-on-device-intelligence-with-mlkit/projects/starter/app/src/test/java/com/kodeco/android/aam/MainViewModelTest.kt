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
