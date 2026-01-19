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
        strategy.deleteData(mockS3Client, S3TestConstants.ROW_LEVEL_DELETION_TARGET_JSONL, invalidDeletionKey)
      }
    }
    assertEquals("Deletion row attribute value must be non-null", exception.message)
    verifyNoInteractions(mockS3Client)
  }

  @Test
  fun deleteData_withJsonlFormat_throwsTodoException() {
    val deletionTarget = S3TestConstants.ROW_LEVEL_DELETION_TARGET_JSONL.copy(
      objectFileFormat = com.ondemanddeletionplatform.deletion.models.s3.FileFormat.JSONL
    )
    val deletionKey = com.ondemanddeletionplatform.deletion.models.s3.S3DeletionKeyValue(
      deletionKeyPatternCaptureValue = S3TestConstants.CUSTOMER_ID,
      deletionRowAttributeValue = S3TestConstants.CUSTOMER_ID
    )

    val exception = assertThrows(NotImplementedError::class.java) {
      runBlocking {
        strategy.deleteData(mockS3Client, deletionTarget, deletionKey)
      }
    }
    assertEquals("An operation is not implemented: JSON Line row-level deletion not yet implemented", exception.message)
  }

  @Test
  fun deleteData_withParquetFormat_throwsTodoException() {
    val deletionTarget = S3TestConstants.ROW_LEVEL_DELETION_TARGET_JSONL.copy(
      objectFileFormat = com.ondemanddeletionplatform.deletion.models.s3.FileFormat.PARQUET
    )
    val deletionKey = com.ondemanddeletionplatform.deletion.models.s3.S3DeletionKeyValue(
      deletionKeyPatternCaptureValue = S3TestConstants.CUSTOMER_ID,
      deletionRowAttributeValue = S3TestConstants.CUSTOMER_ID
    )

    val exception = assertThrows(NotImplementedError::class.java) {
      runBlocking {
        strategy.deleteData(mockS3Client, deletionTarget, deletionKey)
      }
    }
    assertEquals("An operation is not implemented: Parquet row-level deletion not yet implemented", exception.message)
  }
}
