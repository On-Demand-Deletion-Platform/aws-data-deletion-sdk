package com.ondemanddeletionplatform.deletionworker.domain.connectors

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.ondemanddeletionplatform.deletionworker.domain.models.DynamoDbDeletionTarget
import com.ondemanddeletionplatform.deletionworker.domain.models.ValidatedDynamoDbTableKeyDeletionTarget

/**
 * DynamoDB on-demand-deletion strategy that deletes items based on table key.
 */
class DynamoDbTableKeyDeletionStrategy : DynamoDbDeletionStrategy() {
  override suspend fun deleteCustomer(ddb: DynamoDbClient, deletionTarget: DynamoDbDeletionTarget, customerId: String) {
    println("Called partition key deletion for customerId: $customerId")
    val validatedDeletionTarget = ValidatedDynamoDbTableKeyDeletionTarget.fromDeletionTarget(deletionTarget)

    val tableKey = mutableMapOf<String, AttributeValue>()
    tableKey[validatedDeletionTarget.partitionKeyName] = AttributeValue.S(customerId)

    deleteCustomerByKey(ddb, deletionTarget, tableKey)
  }
}
