package com.ondemanddeletionplatform.deletion.models.internal.s3

import com.ondemanddeletionplatform.deletion.models.s3.FileFormat
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionStrategyType
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionTarget
import java.util.regex.Pattern

/**
 * Validated data model for the S3 row level deletion strategy.
 *
 * Used for deleting matching rows containing specific attribute values
 * from S3 files that contain data for multiple customers.
 */
internal data class ValidatedS3RowLevelDeletionTarget(
  val strategy: S3DeletionStrategyType,
  val awsRegion: String,
  val bucketName: String,
  val objectKeyPrefix: String?,
  val deletionKeyPattern: Pattern?,
  val deletionRowAttributeName: String,
  val objectFileFormat: FileFormat
) {
  companion object {
    fun fromDeletionTarget(deletionTarget: S3DeletionTarget): ValidatedS3RowLevelDeletionTarget {
      require(deletionTarget.strategy == S3DeletionStrategyType.ROW_LEVEL) {
        "Deletion target strategy must be ROW_LEVEL"
      }

      requireNotNull(deletionTarget.deletionRowAttributeName) { "Deletion row attribute name must not be null" }
      requireNotNull(deletionTarget.objectFileFormat) { "Object file format must not be null" }

      if (deletionTarget.deletionKeyPattern != null) {
        require(deletionTarget.deletionKeyPattern.matcher("").groupCount() == 1) {
          "Deletion key pattern must have exactly one capture group"
        }
      }

      return ValidatedS3RowLevelDeletionTarget(
        strategy = deletionTarget.strategy,
        awsRegion = deletionTarget.awsRegion,
        bucketName = deletionTarget.bucketName,
        objectKeyPrefix = deletionTarget.objectKeyPrefix,
        deletionKeyPattern = deletionTarget.deletionKeyPattern,
        deletionRowAttributeName = deletionTarget.deletionRowAttributeName,
        objectFileFormat = deletionTarget.objectFileFormat
      )
    }
  }
}
