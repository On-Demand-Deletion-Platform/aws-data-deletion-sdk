package com.ondemanddeletionplatform.deletion.connectors.s3.internal

import aws.sdk.kotlin.services.s3.S3Client
import com.ondemanddeletionplatform.deletion.models.internal.s3.ValidatedS3RowLevelDeletionTarget
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionKeyValue
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionTarget

/**
 * S3 on-demand deletion strategy that deletes S3 file rows with matching attribute values.
 */
internal class S3RowLevelDeletionStrategy : S3DeletionStrategy() {
  override suspend fun deleteData(s3: S3Client, deletionTarget: S3DeletionTarget, deletionKey: S3DeletionKeyValue) {
    ValidatedS3RowLevelDeletionTarget.fromDeletionTarget(deletionTarget)
    requireNotNull(deletionKey.deletionRowAttributeValue) {
      "Deletion row attribute value must be non-null"
    }
    TODO("S3 row-level deletion strategy not yet implemented")
  }
}
