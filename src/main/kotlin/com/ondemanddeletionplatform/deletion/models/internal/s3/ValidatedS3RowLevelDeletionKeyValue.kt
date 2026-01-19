package com.ondemanddeletionplatform.deletion.models.internal.s3

import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionKeyValue

/**
 * Validated data model for a S3 row-level deletion key value.
 */
internal data class ValidatedS3RowLevelDeletionKeyValue(
  val objectKeyPrefix: String?,
  val deletionKeyPatternCaptureValue: String?,
  val deletionRowAttributeValue: String
) {
  companion object {
    fun fromDeletionKeyValue(deletionKey: S3DeletionKeyValue): ValidatedS3RowLevelDeletionKeyValue {
      requireNotNull(deletionKey.deletionRowAttributeValue) {
        "Deletion row attribute value must be non-null"
      }
      return ValidatedS3RowLevelDeletionKeyValue(
        objectKeyPrefix = deletionKey.objectKeyPrefix,
        deletionKeyPatternCaptureValue = deletionKey.deletionKeyPatternCaptureValue,
        deletionRowAttributeValue = deletionKey.deletionRowAttributeValue
      )
    }
  }
}
