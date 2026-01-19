package com.ondemanddeletionplatform.deletion.connectors.s3.internal

import aws.sdk.kotlin.services.s3.S3Client
import com.ondemanddeletionplatform.deletion.testutil.S3TestConstants
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock

class S3JsonLineRowLevelDeletionStrategyTest {
  private val mockS3Client: S3Client = mock()
  private val strategy = S3JsonLineRowLevelDeletionStrategy()

  @Test
  fun deleteData_unvalidatedMethodThrowsRedirectException() {
    val exception = assertThrows(NotImplementedError::class.java) {
      runBlocking {
        strategy.deleteData(mockS3Client, S3TestConstants.ROW_LEVEL_DELETION_TARGET, S3TestConstants.ROW_LEVEL_DELETION_KEY)
      }
    }
    assertEquals(S3JsonLineRowLevelDeletionStrategy.USE_VALIDATED_DELETE_METHOD_MESSAGE, exception.message)
  }
}
