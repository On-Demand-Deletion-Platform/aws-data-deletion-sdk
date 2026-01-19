package com.ondemanddeletionplatform.deletion.connectors.s3.internal

import aws.sdk.kotlin.services.s3.S3Client
import com.ondemanddeletionplatform.deletion.models.internal.s3.ValidatedS3RowLevelDeletionKeyValue
import com.ondemanddeletionplatform.deletion.models.internal.s3.ValidatedS3RowLevelDeletionTarget
import com.ondemanddeletionplatform.deletion.models.s3.FileFormat
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionKeyValue
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionTarget

/**
 * S3 on-demand deletion strategy that deletes S3 file rows with matching attribute values.
 */
internal class S3RowLevelDeletionStrategy : S3DeletionStrategy() {
  override suspend fun deleteData(s3: S3Client, deletionTarget: S3DeletionTarget, deletionKey: S3DeletionKeyValue) {
    val rowDeletionTarget = ValidatedS3RowLevelDeletionTarget.fromDeletionTarget(deletionTarget)
    val rowDeletionKeyValue = ValidatedS3RowLevelDeletionKeyValue.fromDeletionKeyValue(deletionKey)

    when (rowDeletionTarget.objectFileFormat) {
      FileFormat.JSONL -> S3JsonLineRowLevelDeletionStrategy().deleteData(s3, rowDeletionTarget, rowDeletionKeyValue)
      FileFormat.PARQUET -> S3ParquetRowLevelDeletionStrategy().deleteData(s3, rowDeletionTarget, rowDeletionKeyValue)
    }
  }
}
