package com.ondemanddeletionplatform.deletionworker.domain.connectors

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.ondemanddeletionplatform.deletionworker.domain.models.DynamoDbDeletionKeyValue
import com.ondemanddeletionplatform.deletionworker.domain.models.DynamoDbDeletionTarget
import com.ondemanddeletionplatform.deletionworker.domain.models.ValidatedDynamoDbTableKeyDeletionTarget

/**
 * DynamoDB on-demand-deletion strategy that deletes items based on table key.
 */
class DynamoDbTableKeyDeletionStrategy : DynamoDbDeletionStrategy() {
  override suspend fun deleteData(ddb: DynamoDbClient, deletionTarget: DynamoDbDeletionTarget, deletionKey: DynamoDbDeletionKeyValue) {
    println("Called partition key deletion for deletionKey: $deletionKey")
    val validatedDeletionTarget = ValidatedDynamoDbTableKeyDeletionTarget.fromDeletionTarget(deletionTarget)

    val tableKey = mutableMapOf<String, AttributeValue>()
    tableKey[validatedDeletionTarget.partitionKeyName] = AttributeValue.S(deletionKey.primaryKeyValue)

    if (
      validatedDeletionTarget.sortKeyName != null &&
      validatedDeletionTarget.deletionKeySchema.secondaryKeyName != null &&
      deletionKey.secondaryKeyValue != null
    ) {
      tableKey[validatedDeletionTarget.sortKeyName] = AttributeValue.S(deletionKey.secondaryKeyValue)
    }

    deleteDataByKey(ddb, deletionTarget, tableKey)
  }
}
