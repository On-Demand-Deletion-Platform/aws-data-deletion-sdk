package com.ondemanddeletionplatform.deletion.connectors.s3.internal

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.DeleteObjectsRequest
import aws.sdk.kotlin.services.s3.model.DeleteObjectsResponse
import aws.sdk.kotlin.services.s3.model.Error
import aws.sdk.kotlin.services.s3.model.ListObjectsV2Request
import aws.sdk.kotlin.services.s3.model.ListObjectsV2Response
import aws.sdk.kotlin.services.s3.model.Object
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionKeyValue
import com.ondemanddeletionplatform.deletion.testutil.S3TestConstants
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

class S3ObjectKeyDeletionStrategyTest {
  private val mockS3Client: S3Client = mock()
  private val strategy = S3ObjectKeyDeletionStrategy()

  @Test
  fun deleteData_withoutRequiredDeletionKeyAttributes_throwsException() {
    val invalidDeletionKey = S3DeletionKeyValue(deletionKeyPatternCaptureValue = null)

    val exception = assertThrows(IllegalArgumentException::class.java) {
      runBlocking {
        strategy.deleteData(mockS3Client, S3TestConstants.OBJECT_KEY_DELETION_TARGET, invalidDeletionKey)
      }
    }
    assertEquals("S3 deletion key must provide deletionKeyPatternCaptureValue or objectKeyPrefix", exception.message)
    verifyNoInteractions(mockS3Client)
  }

  @Test
  fun deleteData_withCaptureValue_replacesPatternAndDeletesMatchingObjects() {
    val deleteCaptor = argumentCaptor<DeleteObjectsRequest>()

    runBlocking {
      whenever(mockS3Client.listObjectsV2(any<ListObjectsV2Request>())).thenReturn(
        ListObjectsV2Response {
          contents = listOf(
            Object { key = S3TestConstants.MATCHING_OBJECT_KEY_1 },
            Object { key = "data/customers/othercustomer/file.json" },
            Object { key = S3TestConstants.MATCHING_OBJECT_KEY_2 }
          )
          isTruncated = false
        }
      )
      mockDeleteObjectsSuccess()

      strategy.deleteData(mockS3Client, S3TestConstants.OBJECT_KEY_DELETION_TARGET, S3TestConstants.DELETION_KEY_CAPTURE_VALUE_ONLY)
      verify(mockS3Client, times(1)).deleteObjects(deleteCaptor.capture())
    }

    val deleteRequest = deleteCaptor.firstValue
    assertEquals(S3TestConstants.BUCKET_NAME, deleteRequest.bucket)
    assertEquals(2, deleteRequest.delete?.objects?.size)
    assertEquals(S3TestConstants.MATCHING_OBJECT_KEY_1, deleteRequest.delete?.objects?.first()?.key)
    assertEquals(S3TestConstants.MATCHING_OBJECT_KEY_2, deleteRequest.delete?.objects?.get(1)?.key)
  }

  @Test
  fun deleteData_withoutCaptureValue_usesOriginalPatternAndDeletesMatchingObjects() {
    val deletionKey = S3DeletionKeyValue(
      objectKeyPrefix = "data/customers/",
      deletionKeyPatternCaptureValue = null
    )
    val matchingObject = Object { key = "data/customers/anyvalue/file1.json" }

    val deleteCaptor = argumentCaptor<DeleteObjectsRequest>()

    runBlocking {
      whenever(mockS3Client.listObjectsV2(any<ListObjectsV2Request>())).thenReturn(
        ListObjectsV2Response {
          contents = listOf(matchingObject)
          isTruncated = false
        }
      )
      mockDeleteObjectsSuccess()
      strategy.deleteData(mockS3Client, S3TestConstants.OBJECT_KEY_DELETION_TARGET, deletionKey)
      verify(mockS3Client).deleteObjects(deleteCaptor.capture())
    }

    val deleteRequest = deleteCaptor.firstValue
    assertEquals(1, deleteRequest.delete?.objects?.size)
    assertEquals("data/customers/anyvalue/file1.json", deleteRequest.delete?.objects?.first()?.key)
  }

  @Test
  fun deleteData_withDeletionKeyPrefix_queriesByKeyPrefix() {
    val deletionKey = S3DeletionKeyValue(
      objectKeyPrefix = "custom/prefix/",
      deletionKeyPatternCaptureValue = S3TestConstants.CUSTOMER_ID
    )

    runBlocking {
      whenever(mockS3Client.listObjectsV2(any<ListObjectsV2Request>())).thenReturn(
        ListObjectsV2Response {
          contents = emptyList()
          isTruncated = false
        }
      )
      strategy.deleteData(mockS3Client, S3TestConstants.OBJECT_KEY_DELETION_TARGET, deletionKey)

      val listCaptor = argumentCaptor<ListObjectsV2Request>()
      verify(mockS3Client).listObjectsV2(listCaptor.capture())
      assertEquals("custom/prefix/", listCaptor.firstValue.prefix)
    }
  }

  @Test
  fun deleteData_withTargetPrefix_usesTargetPrefixIfNoDeletionKeyPrefix() {
    val listCaptor = argumentCaptor<ListObjectsV2Request>()

    runBlocking {
      whenever(mockS3Client.listObjectsV2(any<ListObjectsV2Request>())).thenReturn(
        ListObjectsV2Response {
          contents = emptyList()
          isTruncated = false
        }
      )
      mockDeleteObjectsSuccess()
      strategy.deleteData(mockS3Client, S3TestConstants.OBJECT_KEY_DELETION_TARGET, S3TestConstants.DELETION_KEY_CAPTURE_VALUE_ONLY)

      verify(mockS3Client).listObjectsV2(listCaptor.capture())
    }

    val listRequest = listCaptor.firstValue
    assertEquals(S3TestConstants.OBJECT_KEY_PREFIX, listRequest.prefix)
  }

  @Test
  fun deleteData_withPaginatedResults_handlesMultiplePages() {
    val deleteCaptor = argumentCaptor<DeleteObjectsRequest>()

    runBlocking {
      whenever(mockS3Client.listObjectsV2(any<ListObjectsV2Request>()))
        .thenReturn(
          ListObjectsV2Response {
            contents = listOf(Object { key = S3TestConstants.MATCHING_OBJECT_KEY_1 })
            isTruncated = true
            nextContinuationToken = "token123"
          }
        )
        .thenReturn(
          ListObjectsV2Response {
            contents = listOf(Object { key = S3TestConstants.MATCHING_OBJECT_KEY_2 })
            isTruncated = false
          }
        )
      mockDeleteObjectsSuccess()

      strategy.deleteData(mockS3Client, S3TestConstants.OBJECT_KEY_DELETION_TARGET, S3TestConstants.DELETION_KEY_CAPTURE_VALUE_ONLY)

      verify(mockS3Client, times(2)).listObjectsV2(any<ListObjectsV2Request>())
      verify(mockS3Client, times(2)).deleteObjects(deleteCaptor.capture())
    }

    val deleteRequest1 = deleteCaptor.firstValue
    assertEquals(S3TestConstants.BUCKET_NAME, deleteRequest1.bucket)
    assertEquals(S3TestConstants.MATCHING_OBJECT_KEY_1, deleteRequest1.delete?.objects?.first()?.key)

    val deleteRequest2 = deleteCaptor.secondValue
    assertEquals(S3TestConstants.BUCKET_NAME, deleteRequest2.bucket)
    assertEquals(S3TestConstants.MATCHING_OBJECT_KEY_2, deleteRequest2.delete?.objects?.first()?.key)
  }

  @Test
  fun deleteData_withNoMatchingObjects_doesNotCallDelete() {
    val nonMatchingObject = Object { key = "data/other/file.json" }

    runBlocking {
      whenever(mockS3Client.listObjectsV2(any<ListObjectsV2Request>())).thenReturn(
        ListObjectsV2Response {
          contents = listOf(nonMatchingObject)
          isTruncated = false
        }
      )
      strategy.deleteData(mockS3Client, S3TestConstants.OBJECT_KEY_DELETION_TARGET, S3TestConstants.DELETION_KEY_CAPTURE_VALUE_ONLY)

      verify(mockS3Client, never()).deleteObjects(any<DeleteObjectsRequest>())
    }
  }

  @Test
  fun deleteData_withEmptyContents_doesNotCallDelete() {
    runBlocking {
      whenever(mockS3Client.listObjectsV2(any<ListObjectsV2Request>())).thenReturn(
        ListObjectsV2Response {
          contents = emptyList()
          isTruncated = false
        }
      )

      strategy.deleteData(mockS3Client, S3TestConstants.OBJECT_KEY_DELETION_TARGET, S3TestConstants.DELETION_KEY_CAPTURE_VALUE_ONLY)

      verify(mockS3Client, never()).deleteObjects(any<DeleteObjectsRequest>())
    }
  }

  @Test
  fun deleteData_withNullContents_doesNotCallDelete() {
    runBlocking {
      whenever(mockS3Client.listObjectsV2(any<ListObjectsV2Request>())).thenReturn(
        ListObjectsV2Response {
          contents = null
          isTruncated = false
        }
      )

      strategy.deleteData(mockS3Client, S3TestConstants.OBJECT_KEY_DELETION_TARGET, S3TestConstants.DELETION_KEY_CAPTURE_VALUE_ONLY)

      verify(mockS3Client, never()).deleteObjects(any<DeleteObjectsRequest>())
    }
  }

  @Test
  fun deleteData_withDeleteObjectsErrors_throwsException() {
    runBlocking {
      whenever(mockS3Client.listObjectsV2(any<ListObjectsV2Request>())).thenReturn(
        ListObjectsV2Response {
          contents = listOf(Object { key = S3TestConstants.MATCHING_OBJECT_KEY_1 })
          isTruncated = false
        }
      )
      whenever(mockS3Client.deleteObjects(any<DeleteObjectsRequest>())).thenReturn(
        DeleteObjectsResponse {
          errors = listOf(
            Error {
              key = S3TestConstants.MATCHING_OBJECT_KEY_1
              code = "AccessDenied"
              message = "Access denied for object"
            }
          )
        }
      )
      val exception = assertThrows(IllegalStateException::class.java) {
        runBlocking {
          strategy.deleteData(mockS3Client, S3TestConstants.OBJECT_KEY_DELETION_TARGET, S3TestConstants.DELETION_KEY_CAPTURE_VALUE_ONLY)
        }
      }
      assertNotNull(exception.message)
      assertTrue(exception.message!!.startsWith("Received errors in S3 DeleteObjects response"))
    }
  }

  private suspend fun mockDeleteObjectsSuccess() {
    whenever(mockS3Client.deleteObjects(any<DeleteObjectsRequest>())).thenReturn(
      DeleteObjectsResponse {}
    )
  }
}
