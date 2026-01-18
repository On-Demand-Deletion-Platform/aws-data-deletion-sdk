package com.ondemanddeletionplatform.deletion.connectors.s3.internal

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.Delete
import aws.sdk.kotlin.services.s3.model.DeleteObjectsRequest
import aws.sdk.kotlin.services.s3.model.ListObjectsV2Request
import aws.sdk.kotlin.services.s3.model.ListObjectsV2Response
import aws.sdk.kotlin.services.s3.model.ObjectIdentifier
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionKeyValue
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionTarget
import java.util.regex.Pattern

/**
 * Base class for S3 on-demand-deletion strategies.
 */
internal abstract class S3DeletionStrategy {
  abstract suspend fun deleteData(s3: S3Client, deletionTarget: S3DeletionTarget, deletionKey: S3DeletionKeyValue)

  /**
   * Query the S3 ListObjectsV2 API with an optional key prefix and pagination token.
   */
  protected suspend fun listS3Objects(
    s3: S3Client,
    bucketName: String,
    objectKeyPrefix: String?,
    continuationToken: String?
  ): ListObjectsV2Response {
    return s3.listObjectsV2(
      ListObjectsV2Request {
        bucket = bucketName
        prefix = objectKeyPrefix
        this.continuationToken = continuationToken
      }
    )
  }

  /**
   * Delete all specified S3 objects.
   */
  protected suspend fun deleteS3Objects(s3: S3Client, bucketName: String, objectIds: List<ObjectIdentifier>) {
    if (objectIds.isEmpty()) {
      return
    }
    println("Submitting S3 DeleteObjects request for objectIds: $objectIds")
    val deleteResponse = s3.deleteObjects(
      DeleteObjectsRequest {
        bucket = bucketName
        delete = Delete {
          objects = objectIds
        }
      }
    )
    check(deleteResponse.errors.isNullOrEmpty()) {
      "Received errors in S3 DeleteObjects response: ${deleteResponse.errors}"
    }
  }

  /**
   * Parse S3 ListObjects API response for object keys matching the deletion key pattern.
   */
  protected fun getObjectKeysMatchingDeletionPattern(
    listObjectsResponse: ListObjectsV2Response,
    deletionKeyPattern: Pattern?
  ): List<ObjectIdentifier> {
    val listObjectsContents = listObjectsResponse.contents ?: return emptyList()
    return listObjectsContents.mapNotNull { obj ->
      if (deletionKeyPattern == null || deletionKeyPattern.matcher(obj.key ?: "").matches()) {
        ObjectIdentifier { key = obj.key }
      } else {
        null
      }
    }
  }

  /**
   * Construct the most precise deletion key pattern available from a base
   * S3 object key pattern with a single capture group, and an optional
   * exact string value to use in place of that capture group.
   *
   * Eg.
   * constructEffectiveDeletionKeyPattern(
   *   Pattern.compile("data/customers/(\\w+)/"),
   *   "fred"
   * ) -> Pattern.compile("data/customers/fred/")
   */
  protected fun constructEffectiveDeletionKeyPattern(basePattern: Pattern, captureValue: String?): Pattern {
    if (captureValue == null) {
      return basePattern
    }
    // Replace capture group with specific string, eg. converting a customer ID pattern to a specific customer ID
    val updatedPatternStr = basePattern.pattern().replace(Regex("\\([^?][^)]*\\)"), captureValue)
    return Pattern.compile(updatedPatternStr)
  }
}
