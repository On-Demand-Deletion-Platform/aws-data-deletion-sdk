package com.ondemanddeletionplatform.deletionworker

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertNotNull

class DeletionWorkerTest {
  @Test
  fun deletionWorkerHasAGreeting() {
    val classUnderTest = DeletionWorker()
    assertNotNull(classUnderTest.greeting, "deletion worker should have a greeting")
  }
}
