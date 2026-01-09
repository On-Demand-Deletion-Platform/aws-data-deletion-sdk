package com.ondemanddeletionplatform.deletionworker.domain.connectors.dynamodb

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.DeleteItemRequest
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.DynamoDbDeletionKeyValue
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.DynamoDbDeletionTarget

/**
 * Base class for DynamoDB on-demand-deletion strategies.
 */
abstract class DynamoDbDeletionStrategy {
  abstract suspend fun deleteData(ddb: DynamoDbClient, deletionTarget: DynamoDbDeletionTarget, deletionKey: DynamoDbDeletionKeyValue)

  protected suspend fun deleteDataByKey(
    ddb: DynamoDbClient,
    tableName: String,
    tableKey: Map<String, AttributeValue>
  ) {
    val deleteItemRequest = DeleteItemRequest {
      this.tableName = tableName
      key = tableKey
    }
    ddb.deleteItem(deleteItemRequest)
  }
}
