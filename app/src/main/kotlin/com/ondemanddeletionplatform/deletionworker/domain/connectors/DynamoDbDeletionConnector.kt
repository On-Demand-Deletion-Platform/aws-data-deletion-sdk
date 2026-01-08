package com.ondemanddeletionplatform.deletionworker.domain.connectors

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import com.ondemanddeletionplatform.deletionworker.domain.models.DynamoDbDeletionStrategyType
import com.ondemanddeletionplatform.deletionworker.domain.models.DynamoDbDeletionTarget
import com.ondemanddeletionplatform.deletionworker.domain.models.ValidatedDynamoDbGsiDeletionTarget
import com.ondemanddeletionplatform.deletionworker.domain.models.ValidatedDynamoDbScanDeletionTarget

/**
 * Used to execute on-demand data deletion requests
 * across onboarded DynamoDB deletion targets.
 */
class DynamoDbDeletionConnector(val ddb: DynamoDbClient) {
  suspend fun deleteCustomer(deletionTarget: DynamoDbDeletionTarget, customerId: String) {
    when (deletionTarget.strategy) {
      DynamoDbDeletionStrategyType.TABLE_KEY -> DynamoDbTableKeyDeletionStrategy().deleteCustomer(ddb, deletionTarget, customerId)
      DynamoDbDeletionStrategyType.GSI_QUERY -> deleteCustomerByGsiKey(deletionTarget, customerId)
      DynamoDbDeletionStrategyType.SCAN -> deleteCustomerByScan(deletionTarget, customerId)
    }
  }

  private suspend fun deleteCustomerByGsiKey(deletionTarget: DynamoDbDeletionTarget, customerId: String) {
    println("Called GSI deletion for customerId: $customerId")
    ValidatedDynamoDbGsiDeletionTarget.fromDeletionTarget(deletionTarget)
    throw NotImplementedError("GSI deletion strategy not yet implemented")
  }

  private suspend fun deleteCustomerByScan(deletionTarget: DynamoDbDeletionTarget, customerId: String) {
    println("Called scan deletion for customerId: $customerId")
    ValidatedDynamoDbScanDeletionTarget.fromDeletionTarget(deletionTarget)
    throw NotImplementedError("Scan deletion strategy not yet implemented")
  }
}
