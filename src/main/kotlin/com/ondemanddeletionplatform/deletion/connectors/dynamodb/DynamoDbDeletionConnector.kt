package com.ondemanddeletionplatform.deletion.connectors.dynamodb

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import com.ondemanddeletionplatform.deletion.connectors.dynamodb.internal.DynamoDbGsiKeyDeletionStrategy
import com.ondemanddeletionplatform.deletion.connectors.dynamodb.internal.DynamoDbScanDeletionStrategy
import com.ondemanddeletionplatform.deletion.connectors.dynamodb.internal.DynamoDbTableKeyDeletionStrategy
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionKeyValue
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionStrategyType
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionTarget

/**
 * This connector provides a unified interface for deleting data from DynamoDB tables
 * with the deletion strategy based on the deletion target configuration.
 *
 * @property ddb The DynamoDB client used for executing deletion operations
 */
class DynamoDbDeletionConnector(val ddb: DynamoDbClient) {

  /**
   * Deletes data from a DynamoDB table based on the specified deletion target and key.
   *
   * The deletion strategy is determined by the target's strategy type:
   * - TABLE_KEY: Deletes items by primary key (most efficient)
   * - GSI_QUERY: Queries a Global Secondary Index and deletes matching items
   * - SCAN: Scans the entire table and deletes matching items (least efficient)
   *
   * @param deletionTarget Configuration specifying the table, strategy, and key schema
   * @param deletionKey The specific key values to match for deletion
   * @throws Exception if the deletion operation fails or if required parameters are missing
   */
  suspend fun deleteData(deletionTarget: DynamoDbDeletionTarget, deletionKey: DynamoDbDeletionKeyValue) {
    when (deletionTarget.strategy) {
      DynamoDbDeletionStrategyType.TABLE_KEY -> DynamoDbTableKeyDeletionStrategy().deleteData(ddb, deletionTarget, deletionKey)
      DynamoDbDeletionStrategyType.GSI_QUERY -> DynamoDbGsiKeyDeletionStrategy().deleteData(ddb, deletionTarget, deletionKey)
      DynamoDbDeletionStrategyType.SCAN -> DynamoDbScanDeletionStrategy().deleteData(ddb, deletionTarget, deletionKey)
    }
  }
}
