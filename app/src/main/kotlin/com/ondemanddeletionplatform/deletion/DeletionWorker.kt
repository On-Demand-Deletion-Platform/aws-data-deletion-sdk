package com.ondemanddeletionplatform.deletion

/**
 * Main class for the On Demand Deletion worker.
 *
 * This class will poll for deletion requests and execute
 * them across all onboarded data stores.
 */
class DeletionWorker {
  val greeting: String
    get() {
      return "Hello World!"
    }
}

fun main() {
  println(DeletionWorker().greeting)
}
