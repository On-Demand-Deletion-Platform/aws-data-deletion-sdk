package com.ondemanddeletionplatform.deletionworker.domain.connectors

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.DeleteItemRequest
import com.ondemanddeletionplatform.deletionworker.domain.models.DynamoDbDeletionStrategy
import com.ondemanddeletionplatform.deletionworker.domain.models.DynamoDbDeletionTarget
import com.ondemanddeletionplatform.deletionworker.domain.models.ValidatedDynamoDbGsiDeletionTarget
import com.ondemanddeletionplatform.deletionworker.domain.models.ValidatedDynamoDbScanDeletionTarget
import com.ondemanddeletionplatform.deletionworker.domain.models.ValidatedDynamoDbTableKeyDeletionTarget

/**
 * Used to execute on-demand data deletion requests
 * across onboarded DynamoDB deletion targets.
 */
class DynamoDbDeletionConnector(val ddb: DynamoDbClient) {
  suspend fun deleteCustomer(deletionTarget: DynamoDbDeletionTarget, customerId: String) {
    when (deletionTarget.strategy) {
      DynamoDbDeletionStrategy.TABLE_KEY -> deleteCustomerByPartitionKey(deletionTarget, customerId)
      DynamoDbDeletionStrategy.GSI_QUERY -> deleteCustomerByGsiKey(deletionTarget, customerId)
      DynamoDbDeletionStrategy.SCAN -> deleteCustomerByScan(deletionTarget, customerId)
    }
  }

  private suspend fun deleteCustomerByPartitionKey(deletionTarget: DynamoDbDeletionTarget, customerId: String) {
    println("Called partition key deletion for customerId: $customerId")
    val validatedDeletionTarget = ValidatedDynamoDbTableKeyDeletionTarget.fromDeletionTarget(deletionTarget)

    val tableKey = mutableMapOf<String, AttributeValue>()
    tableKey[validatedDeletionTarget.partitionKeyName] = AttributeValue.S(customerId)
    if (validatedDeletionTarget.sortKeyName != null && validatedDeletionTarget.sortKeyValue != null) {
      tableKey[validatedDeletionTarget.sortKeyName] = AttributeValue.S(validatedDeletionTarget.sortKeyValue)
    }

    deleteCustomerByKey(deletionTarget, tableKey)
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

  private suspend fun deleteCustomerByKey(deletionTarget: DynamoDbDeletionTarget, tableKey: Map<String, AttributeValue>) {
    val deleteItemRequest = DeleteItemRequest {
      tableName = deletionTarget.tableName
      key = tableKey
    }
    ddb.deleteItem(deleteItemRequest)
  }
}
