package com.ondemanddeletionplatform.deletionworker.domain.connectors.dynamodb

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.DynamoDbDeletionKeyValue
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.DynamoDbDeletionTarget
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.ValidatedDynamoDbGsiDeletionTarget

/**
 * DynamoDB on-demand-deletion strategy that deletes items based on GSI key.
 */
class DynamoDbGsiKeyDeletionStrategy : DynamoDbDeletionStrategy() {
  override suspend fun deleteData(ddb: DynamoDbClient, deletionTarget: DynamoDbDeletionTarget, deletionKey: DynamoDbDeletionKeyValue) {
    println("Called GSI deletion for deletionKey: $deletionKey")
    ValidatedDynamoDbGsiDeletionTarget.fromDeletionTarget(deletionTarget)
    throw NotImplementedError("GSI deletion strategy not yet implemented")
  }
}
