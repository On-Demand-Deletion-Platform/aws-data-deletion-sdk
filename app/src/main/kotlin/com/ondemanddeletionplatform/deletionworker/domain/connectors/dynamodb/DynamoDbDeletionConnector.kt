package com.ondemanddeletionplatform.deletionworker.domain.connectors.dynamodb

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.DynamoDbDeletionKeyValue
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.DynamoDbDeletionStrategyType
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.DynamoDbDeletionTarget

/**
 * Used to execute on-demand data deletion requests
 * across onboarded DynamoDB deletion targets.
 */
class DynamoDbDeletionConnector(val ddb: DynamoDbClient) {
  suspend fun deleteData(deletionTarget: DynamoDbDeletionTarget, deletionKey: DynamoDbDeletionKeyValue) {
    when (deletionTarget.strategy) {
      DynamoDbDeletionStrategyType.TABLE_KEY -> DynamoDbTableKeyDeletionStrategy().deleteData(ddb, deletionTarget, deletionKey)
      DynamoDbDeletionStrategyType.GSI_QUERY -> DynamoDbGsiKeyDeletionStrategy().deleteData(ddb, deletionTarget, deletionKey)
      DynamoDbDeletionStrategyType.SCAN -> DynamoDbScanDeletionStrategy().deleteData(ddb, deletionTarget, deletionKey)
    }
  }
}
