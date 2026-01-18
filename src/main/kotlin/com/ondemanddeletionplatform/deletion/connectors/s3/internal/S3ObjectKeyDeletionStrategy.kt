package com.ondemanddeletionplatform.deletion.connectors.s3.internal

import aws.sdk.kotlin.services.s3.S3Client
import com.ondemanddeletionplatform.deletion.models.internal.s3.ValidatedS3ObjectKeyDeletionTarget
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionKeyValue
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionTarget

/**
 * S3 on-demand deletion strategy that deletes objects by S3 key pattern.
 */
internal class S3ObjectKeyDeletionStrategy : S3DeletionStrategy() {
  override suspend fun deleteData(s3: S3Client, deletionTarget: S3DeletionTarget, deletionKey: S3DeletionKeyValue) {
    val objectKeyDeletionTarget = ValidatedS3ObjectKeyDeletionTarget.fromDeletionTarget(deletionTarget)
    require(deletionKey.deletionKeyPatternCaptureValue != null || deletionKey.objectKeyPrefix != null) {
      "S3 deletion key must provide deletionKeyPatternCaptureValue or objectKeyPrefix"
    }
    val deletionKeyPattern = constructEffectiveDeletionKeyPattern(
      objectKeyDeletionTarget.deletionKeyPattern,
      deletionKey.deletionKeyPatternCaptureValue
    )
    val objectKeyPrefix = deletionKey.objectKeyPrefix ?: objectKeyDeletionTarget.objectKeyPrefix
    println("Deleting S3 objects with objectKeyPrefix: $objectKeyPrefix, deletionKeyPattern: $deletionKeyPattern")

    var continuationToken: String? = null
    do {
      val listObjectsResponse = listS3Objects(s3, objectKeyDeletionTarget.bucketName, objectKeyPrefix, continuationToken)
      println("listObjectsResponse: $listObjectsResponse")
      val keysToDelete = getObjectKeysMatchingDeletionPattern(listObjectsResponse, deletionKeyPattern)
      println("S3 object keys matching objectKeyPrefix: $objectKeyPrefix, deletionKeyPattern: $deletionKeyPattern: $keysToDelete")
      deleteS3Objects(s3, objectKeyDeletionTarget.bucketName, keysToDelete)

      continuationToken = listObjectsResponse.nextContinuationToken
    } while (listObjectsResponse.isTruncated == true)
  }
}
