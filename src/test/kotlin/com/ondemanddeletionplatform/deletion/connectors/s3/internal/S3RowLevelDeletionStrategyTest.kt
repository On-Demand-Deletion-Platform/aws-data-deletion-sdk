package com.ondemanddeletionplatform.deletion.connectors.s3.internal

import aws.sdk.kotlin.services.s3.S3Client
import com.ondemanddeletionplatform.deletion.testutil.S3TestConstants
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoInteractions

class S3RowLevelDeletionStrategyTest {
  private val mockS3Client: S3Client = mock()
  private val strategy = S3RowLevelDeletionStrategy()

  @Test
  fun deleteData_withoutRequiredDeletionKeyAttributes_throwsException() {
    val invalidDeletionKey = S3TestConstants.DELETION_KEY_CAPTURE_VALUE_ONLY

    val exception = assertThrows(IllegalArgumentException::class.java) {
      runBlocking {
        strategy.deleteData(mockS3Client, S3TestConstants.ROW_LEVEL_DELETION_TARGET, invalidDeletionKey)
      }
    }
    assertEquals("Deletion row attribute value must be non-null", exception.message)
    verifyNoInteractions(mockS3Client)
  }
}
