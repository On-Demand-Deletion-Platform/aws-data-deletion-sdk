package com.ondemanddeletionplatform.deletion.connectors.s3

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.Delete
import aws.sdk.kotlin.services.s3.model.DeleteObjectsRequest
import aws.sdk.kotlin.services.s3.model.DeleteObjectsResponse
import aws.sdk.kotlin.services.s3.model.ListObjectsV2Request
import aws.sdk.kotlin.services.s3.model.ListObjectsV2Response
import aws.sdk.kotlin.services.s3.model.Object
import aws.sdk.kotlin.services.s3.model.ObjectIdentifier
import com.ondemanddeletionplatform.deletion.testutil.S3TestConstants
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class S3DeletionConnectorTest {
  private val mockS3Client: S3Client = mock()
  private val connector = S3DeletionConnector(mockS3Client)

  @Test
  fun deleteData_withRowLevelStrategy_throwsNotImplementedException() {
    val exception = assertThrows(NotImplementedError::class.java) {
      runBlocking {
        connector.deleteData(S3TestConstants.ROW_LEVEL_DELETION_TARGET, S3TestConstants.DELETION_KEY_CAPTURE_VALUE_ONLY)
      }
    }
    assertEquals("An operation is not implemented: S3 row-level deletion strategy not yet implemented", exception.message)
  }

  @Test
  fun deleteData_withObjectKeyStrategy_successfullyDeletesObjects() {
    runBlocking {
      whenever(mockS3Client.listObjectsV2(any<ListObjectsV2Request>())).thenReturn(
        ListObjectsV2Response {
          contents = listOf(Object { key = S3TestConstants.MATCHING_OBJECT_KEY_1 })
          isTruncated = false
        }
      )
      whenever(mockS3Client.deleteObjects(any<DeleteObjectsRequest>())).thenReturn(
        DeleteObjectsResponse {}
      )

      connector.deleteData(S3TestConstants.OBJECT_KEY_DELETION_TARGET, S3TestConstants.DELETION_KEY_CAPTURE_VALUE_ONLY)

      val expectedDeletionRequest = DeleteObjectsRequest {
        bucket = S3TestConstants.BUCKET_NAME
        delete = Delete {
          objects = listOf(ObjectIdentifier { key = S3TestConstants.MATCHING_OBJECT_KEY_1 })
        }
      }
      verify(mockS3Client).deleteObjects(expectedDeletionRequest)
    }
  }
}
