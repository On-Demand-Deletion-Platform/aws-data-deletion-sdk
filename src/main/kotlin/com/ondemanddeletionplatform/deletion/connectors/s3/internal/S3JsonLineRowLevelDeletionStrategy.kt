package com.ondemanddeletionplatform.deletion.connectors.s3.internal

import aws.sdk.kotlin.services.s3.S3Client
import com.ondemanddeletionplatform.deletion.models.internal.s3.ValidatedS3RowLevelDeletionKeyValue
import com.ondemanddeletionplatform.deletion.models.internal.s3.ValidatedS3RowLevelDeletionTarget

/**
 * S3 deletion strategy for JSON Line files that deletes rows containing matching attribute values.
 */
internal class S3JsonLineRowLevelDeletionStrategy {
  @Suppress("UnusedParameter")
  suspend fun deleteData(
    s3: S3Client,
    deletionTarget: ValidatedS3RowLevelDeletionTarget,
    deletionKey: ValidatedS3RowLevelDeletionKeyValue
  ) {
    TODO("JSON Line row-level deletion not yet implemented")
  }
}
