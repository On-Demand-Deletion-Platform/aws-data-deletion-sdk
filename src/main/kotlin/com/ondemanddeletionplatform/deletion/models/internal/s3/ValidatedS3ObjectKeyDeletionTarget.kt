package com.ondemanddeletionplatform.deletion.models.internal.s3

import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionStrategyType
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionTarget
import java.util.regex.Pattern

/**
 * Validated data model for the S3 object key deletion strategy.
 *
 * Used for data deletion requests that map to deleting all files
 * matching an S3 object key pattern.
 */
internal data class ValidatedS3ObjectKeyDeletionTarget(
  val strategy: S3DeletionStrategyType,
  val awsAccountId: String,
  val awsRegion: String,
  val bucketName: String,
  val objectKeyPrefix: String?,
  val deletionKeyPattern: Pattern
) {
  companion object {
    fun fromDeletionTarget(deletionTarget: S3DeletionTarget): ValidatedS3ObjectKeyDeletionTarget {
      require(deletionTarget.strategy == S3DeletionStrategyType.OBJECT_KEY) {
        "Deletion target strategy must be OBJECT_KEY"
      }
      requireNotNull(deletionTarget.deletionKeyPattern) { "Deletion key pattern must not be null" }
      require(deletionTarget.deletionKeyPattern.matcher("").groupCount() == 1) {
        "Deletion key pattern must have exactly one capture group"
      }

      return ValidatedS3ObjectKeyDeletionTarget(
        strategy = deletionTarget.strategy,
        awsAccountId = deletionTarget.awsAccountId,
        awsRegion = deletionTarget.awsRegion,
        bucketName = deletionTarget.bucketName,
        objectKeyPrefix = deletionTarget.objectKeyPrefix,
        deletionKeyPattern = deletionTarget.deletionKeyPattern
      )
    }
  }
}
