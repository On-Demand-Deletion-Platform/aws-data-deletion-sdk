package com.ondemanddeletionplatform.deletionworker.domain.connectors

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.DeleteItemRequest
import com.ondemanddeletionplatform.deletionworker.domain.models.DynamoDbDeletionKeyValue
import com.ondemanddeletionplatform.deletionworker.domain.models.DynamoDbDeletionTarget

/**
 * Base class for DynamoDB on-demand-deletion strategies.
 */
abstract class DynamoDbDeletionStrategy {
  abstract suspend fun deleteData(ddb: DynamoDbClient, deletionTarget: DynamoDbDeletionTarget, deletionKey: DynamoDbDeletionKeyValue)

  protected suspend fun deleteDataByKey(
    ddb: DynamoDbClient,
    deletionTarget: DynamoDbDeletionTarget,
    tableKey: Map<String, AttributeValue>
  ) {
    val deleteItemRequest = DeleteItemRequest {
      tableName = deletionTarget.tableName
      key = tableKey
    }
    ddb.deleteItem(deleteItemRequest)
  }
}
