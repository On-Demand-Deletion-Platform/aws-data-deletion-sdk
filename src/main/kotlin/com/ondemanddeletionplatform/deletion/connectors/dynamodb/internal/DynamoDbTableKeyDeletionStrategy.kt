package com.ondemanddeletionplatform.deletion.connectors.dynamodb.internal

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionKeyValue
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionTarget
import com.ondemanddeletionplatform.deletion.models.internal.dynamodb.ValidatedDynamoDbTableKeyDeletionTarget

/**
 * DynamoDB on-demand-deletion strategy that deletes items based on table key.
 */
internal class DynamoDbTableKeyDeletionStrategy : DynamoDbDeletionStrategy() {
  override suspend fun deleteData(ddb: DynamoDbClient, deletionTarget: DynamoDbDeletionTarget, deletionKey: DynamoDbDeletionKeyValue) {
    println("Called table key deletion for deletionKey: $deletionKey")
    val validatedDeletionTarget = ValidatedDynamoDbTableKeyDeletionTarget.fromDeletionTarget(deletionTarget)

    val tableKey = mutableMapOf<String, AttributeValue>()
    tableKey[validatedDeletionTarget.partitionKeyName] = AttributeValue.S(deletionKey.primaryKeyValue)

    if (validatedDeletionTarget.sortKeyName != null && deletionKey.secondaryKeyValue != null) {
      tableKey[validatedDeletionTarget.sortKeyName] = AttributeValue.S(deletionKey.secondaryKeyValue)
    }

    deleteDataByKey(ddb, deletionTarget.tableName, tableKey)
  }
}
