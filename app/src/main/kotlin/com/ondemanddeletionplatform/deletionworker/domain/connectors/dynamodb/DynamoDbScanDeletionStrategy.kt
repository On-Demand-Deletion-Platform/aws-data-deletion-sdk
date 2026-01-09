package com.ondemanddeletionplatform.deletionworker.domain.connectors.dynamodb

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.DynamoDbDeletionKeyValue
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.DynamoDbDeletionTarget
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.ValidatedDynamoDbScanDeletionTarget

/**
 * DynamoDB on-demand-deletion strategy that scans the table
 * and deletes all items matching deletion key attributes.
 *
 * Note: This strategy is highly inefficient and should be used
 * only as a last resort when it is not possible to index the
 * table by deletion key.
 */
class DynamoDbScanDeletionStrategy : DynamoDbDeletionStrategy() {
  override suspend fun deleteData(ddb: DynamoDbClient, deletionTarget: DynamoDbDeletionTarget, deletionKey: DynamoDbDeletionKeyValue) {
    println("Called scan deletion for deletionKey: $deletionKey")
    ValidatedDynamoDbScanDeletionTarget.fromDeletionTarget(deletionTarget)
    throw NotImplementedError("Scan deletion strategy not yet implemented")
  }
}
