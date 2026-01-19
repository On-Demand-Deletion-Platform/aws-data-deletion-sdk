package com.ondemanddeletionplatform.deletion.connectors.s3.internal

import aws.sdk.kotlin.services.s3.S3Client
import com.ondemanddeletionplatform.deletion.models.internal.s3.ValidatedS3RowLevelDeletionKeyValue
import com.ondemanddeletionplatform.deletion.models.internal.s3.ValidatedS3RowLevelDeletionTarget

/**
 * S3 deletion strategy for Parquet files that deletes rows containing matching attribute values.
 */
internal class S3ParquetRowLevelDeletionStrategy {
  @Suppress("UnusedParameter")
  suspend fun deleteData(
    s3: S3Client,
    deletionTarget: ValidatedS3RowLevelDeletionTarget,
    deletionKey: ValidatedS3RowLevelDeletionKeyValue
  ) {
    TODO("Parquet row-level deletion not yet implemented")
  }
}
