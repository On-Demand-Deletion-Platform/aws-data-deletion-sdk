package com.ondemanddeletionplatform.deletion.connectors.s3.internal

import aws.sdk.kotlin.services.s3.S3Client
import com.ondemanddeletionplatform.deletion.models.internal.s3.ValidatedS3RowLevelDeletionKeyValue
import com.ondemanddeletionplatform.deletion.models.internal.s3.ValidatedS3RowLevelDeletionTarget
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionKeyValue
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionTarget

/**
 * S3 deletion strategy for JSON Line files that deletes rows containing matching attribute values.
 */
internal class S3JsonLineRowLevelDeletionStrategy : S3DeletionStrategy() {
  companion object {
    const val USE_VALIDATED_DELETE_METHOD_MESSAGE = "Use S3JsonLineRowLevelDeletionStrategy.deleteData(" +
      "S3Client, ValidatedS3RowLevelDeletionTarget, ValidatedS3RowLevelDeletionKeyValue)"
  }

  override suspend fun deleteData(s3: S3Client, deletionTarget: S3DeletionTarget, deletionKey: S3DeletionKeyValue) {
    throw NotImplementedError(USE_VALIDATED_DELETE_METHOD_MESSAGE)
  }

  @Suppress("UnusedParameter")
  suspend fun deleteData(
    s3: S3Client,
    deletionTarget: ValidatedS3RowLevelDeletionTarget,
    deletionKey: ValidatedS3RowLevelDeletionKeyValue
  ) {
    TODO("JSON Line row-level deletion not yet implemented")
  }
}
