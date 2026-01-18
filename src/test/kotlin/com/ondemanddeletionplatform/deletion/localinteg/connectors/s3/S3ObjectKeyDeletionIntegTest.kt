package com.ondemanddeletionplatform.deletion.localinteg.connectors.s3

import com.ondemanddeletionplatform.deletion.connectors.s3.S3DeletionConnector
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionKeyValue
import com.ondemanddeletionplatform.deletion.testutil.S3TestConstants
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Local integ tests for deleting S3 data by object key pattern.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class S3ObjectKeyDeletionIntegTest : S3IntegTest() {
  companion object {
    const val FILE_CONTENT = """{"data": "test"}"""
  }

  @Tag("localIntegTest")
  @Test
  fun objectKeyDeletionStrategyCanDeleteWithCaptureValue() {
    val bucketName = "test-bucket-capture-value"

    val matchingKeys = listOf(
      S3TestConstants.MATCHING_OBJECT_KEY_1,
      "${S3TestConstants.OBJECT_KEY_PREFIX}${S3TestConstants.CUSTOMER_ID}/nested/file3.json"
    )
    val nonMatchingKeys = listOf(
      "${S3TestConstants.OBJECT_KEY_PREFIX}other-customer/file1.json",
      "${S3TestConstants.OBJECT_KEY_PREFIX}other-customer/file2.json",
      "logs/system.log"
    )
    val allObjectKeys = matchingKeys + nonMatchingKeys

    runBlocking {
      createBucket(bucketName)

      allObjectKeys.forEach {
        putObject(bucketName, it, FILE_CONTENT)
      }

      val deletionTarget = S3TestConstants.OBJECT_KEY_DELETION_TARGET.copy(bucketName = bucketName)
      val deletionKey = S3TestConstants.DELETION_KEY_CAPTURE_VALUE_ONLY
      S3DeletionConnector(s3).deleteData(deletionTarget, deletionKey)

      validateObjectsDeleted(bucketName, matchingKeys)
      validateObjectsExist(bucketName, nonMatchingKeys)
    }
  }

  @Tag("localIntegTest")
  @Test
  fun objectKeyDeletionStrategyCanDeleteWithObjectKeyPrefix() {
    val bucketName = "test-bucket-prefix"
    val customerPrefix = "${S3TestConstants.OBJECT_KEY_PREFIX}customer-789/"

    val matchingKeys = listOf(
      "${customerPrefix}file1.json",
      "${customerPrefix}subdir/file2.json"
    )
    val nonMatchingKeys = listOf(
      "${S3TestConstants.OBJECT_KEY_PREFIX}other-customer/file.json",
      "data/other/file.json"
    )
    val allObjectKeys = matchingKeys + nonMatchingKeys

    runBlocking {
      createBucket(bucketName)

      allObjectKeys.forEach {
        putObject(bucketName, it, FILE_CONTENT)
      }

      val deletionTarget = S3TestConstants.OBJECT_KEY_DELETION_TARGET.copy(bucketName = bucketName)
      val deletionKey = S3DeletionKeyValue(
        objectKeyPrefix = customerPrefix,
        deletionKeyPatternCaptureValue = null
      )
      S3DeletionConnector(s3).deleteData(deletionTarget, deletionKey)

      validateObjectsDeleted(bucketName, matchingKeys)
      validateObjectsExist(bucketName, nonMatchingKeys)
    }
  }

  @Tag("localIntegTest")
  @Test
  fun objectKeyDeletionStrategyHandlesEmptyBucket() {
    val bucketName = "test-bucket-empty"

    runBlocking {
      createBucket(bucketName)

      val deletionTarget = S3TestConstants.OBJECT_KEY_DELETION_TARGET.copy(bucketName = bucketName)

      val deletionKey = S3DeletionKeyValue(
        deletionKeyPatternCaptureValue = "nonexistent-customer"
      )

      // Should not throw exception on empty bucket
      S3DeletionConnector(s3).deleteData(deletionTarget, deletionKey)

      validateObjectCount(bucketName, null, 0)
    }
  }
}
