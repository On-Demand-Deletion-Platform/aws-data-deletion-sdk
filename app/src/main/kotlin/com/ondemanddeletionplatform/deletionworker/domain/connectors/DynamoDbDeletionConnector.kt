package com.ondemanddeletionplatform.deletionworker.domain.connectors

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import com.ondemanddeletionplatform.deletionworker.domain.models.DynamoDbDeletionKeyValue
import com.ondemanddeletionplatform.deletionworker.domain.models.DynamoDbDeletionStrategyType
import com.ondemanddeletionplatform.deletionworker.domain.models.DynamoDbDeletionTarget
import com.ondemanddeletionplatform.deletionworker.domain.models.ValidatedDynamoDbGsiDeletionTarget
import com.ondemanddeletionplatform.deletionworker.domain.models.ValidatedDynamoDbScanDeletionTarget

/**
 * Used to execute on-demand data deletion requests
 * across onboarded DynamoDB deletion targets.
 */
class DynamoDbDeletionConnector(val ddb: DynamoDbClient) {
  suspend fun deleteData(deletionTarget: DynamoDbDeletionTarget, deletionKey: DynamoDbDeletionKeyValue) {
    when (deletionTarget.strategy) {
      DynamoDbDeletionStrategyType.TABLE_KEY -> DynamoDbTableKeyDeletionStrategy().deleteData(ddb, deletionTarget, deletionKey)
      DynamoDbDeletionStrategyType.GSI_QUERY -> deleteCustomerByGsiKey(deletionTarget, deletionKey)
      DynamoDbDeletionStrategyType.SCAN -> deleteCustomerByScan(deletionTarget, deletionKey)
    }
  }

  private suspend fun deleteCustomerByGsiKey(deletionTarget: DynamoDbDeletionTarget, deletionKey: DynamoDbDeletionKeyValue) {
    println("Called GSI deletion for deletionKey: $deletionKey")
    ValidatedDynamoDbGsiDeletionTarget.fromDeletionTarget(deletionTarget)
    throw NotImplementedError("GSI deletion strategy not yet implemented")
  }

  private suspend fun deleteCustomerByScan(deletionTarget: DynamoDbDeletionTarget, deletionKey: DynamoDbDeletionKeyValue) {
    println("Called scan deletion for deletionKey: $deletionKey")
    ValidatedDynamoDbScanDeletionTarget.fromDeletionTarget(deletionTarget)
    throw NotImplementedError("Scan deletion strategy not yet implemented")
  }
}
