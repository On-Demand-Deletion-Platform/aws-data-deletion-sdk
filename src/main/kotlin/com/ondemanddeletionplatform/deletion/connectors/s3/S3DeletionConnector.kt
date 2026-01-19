package com.ondemanddeletionplatform.deletion.connectors.s3

import aws.sdk.kotlin.services.s3.S3Client
import com.ondemanddeletionplatform.deletion.connectors.s3.internal.S3ObjectKeyDeletionStrategy
import com.ondemanddeletionplatform.deletion.connectors.s3.internal.S3RowLevelDeletionStrategy
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionKeyValue
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionStrategyType
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionTarget

/**
 * This connector provides a unified interface for deleting data from S3 buckets
 * with the deletion strategy based on the deletion target configuration.
 *
 * @property s3 The S3 client used for executing deletion operations
 */
class S3DeletionConnector(val s3: S3Client) {

  /**
   * Deletes data from an S3 bucket based on the specified deletion target and key.
   *
   * The deletion strategy is determined by the target's strategy type:
   * - OBJECT_KEY: Deletes entire S3 objects matching the key pattern (most efficient)
   * - ROW_LEVEL: Removes specific rows from files containing mixed customer data
   *
   * @param deletionTarget Configuration specifying the bucket, strategy, and key patterns
   * @param deletionKey The specific key values to match for deletion
   * @throws Exception if the deletion operation fails or if required parameters are missing
   */
  suspend fun deleteData(deletionTarget: S3DeletionTarget, deletionKey: S3DeletionKeyValue) {
    when (deletionTarget.strategy) {
      S3DeletionStrategyType.OBJECT_KEY -> S3ObjectKeyDeletionStrategy().deleteData(s3, deletionTarget, deletionKey)
      S3DeletionStrategyType.ROW_LEVEL -> S3RowLevelDeletionStrategy().deleteData(s3, deletionTarget, deletionKey)
    }
  }
}
