package com.ondemanddeletionplatform.deletion

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class DeletionWorkerTest {
  @Test
  fun deletionWorkerHasAGreeting() {
    val classUnderTest = DeletionWorker()
    assertNotNull(classUnderTest.greeting, "deletion worker should have a greeting")
  }
}
