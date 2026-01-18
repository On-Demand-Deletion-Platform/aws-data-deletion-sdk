package com.ondemanddeletionplatform.deletion.connectors.s3

import aws.sdk.kotlin.services.s3.S3Client
import com.ondemanddeletionplatform.deletion.connectors.s3.internal.S3ObjectKeyDeletionStrategy
import com.ondemanddeletionplatform.deletion.connectors.s3.internal.S3RowLevelDeletionStrategy
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionKeyValue
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionStrategyType
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionTarget

/**
 * Used to execute on-demand data deletion requests
 * across onboarded S3 deletion targets.
 */
class S3DeletionConnector(val s3: S3Client) {
  suspend fun deleteData(deletionTarget: S3DeletionTarget, deletionKey: S3DeletionKeyValue) {
    when (deletionTarget.strategy) {
      S3DeletionStrategyType.OBJECT_KEY -> S3ObjectKeyDeletionStrategy().deleteData(s3, deletionTarget, deletionKey)
      S3DeletionStrategyType.ROW_LEVEL -> S3RowLevelDeletionStrategy().deleteData(s3, deletionTarget, deletionKey)
    }
  }
}
