package com.ondemanddeletionplatform.deletion.localinteg.connectors.dynamodb

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.ondemanddeletionplatform.deletion.connectors.dynamodb.DynamoDbDeletionConnector
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionKeySchema
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionKeyValue
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionStrategyType
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionTarget
import com.ondemanddeletionplatform.deletion.testutil.DynamoDbTestConstants
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Local integ tests for deleting DynamoDB data by table key.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DynamoDbDeleteByTableKeyIntegTest : DynamoDbIntegTest() {
  @Tag("localIntegTest")
  @Test
  fun tableKeyDeletionStrategyCanDeleteWithoutSortKey() {
    val tableName = "TestDeleteByPartitionKeyWithoutSortKey"
    runBlocking {
      createCustomerTableWithTestData(tableName)

      val deletionTarget = buildTableKeyDeletionTarget(tableName, null)
      val deletionKeyValue = DynamoDbDeletionKeyValue(
        primaryKeyValue = DynamoDbTestConstants.CUSTOMER_ID_1
      )
      DynamoDbDeletionConnector(dynamoDb).deleteData(deletionTarget, deletionKeyValue)

      validateDeleted(tableName, DynamoDbTestConstants.CUSTOMER_ID_1, null)
      validateNotDeleted(tableName, listOf(DynamoDbTestConstants.CUSTOMER_ID_2), null)
    }
  }

  @Tag("localIntegTest")
  @Test
  fun tableKeyDeletionStrategyCanDeleteWithSortKey() {
    val tableName = "TestDeleteByPartitionKeyWithSortKey"
    val sortKeyVal = DynamoDbTestConstants.SORT_KEY_VALUE
    runBlocking {
      createCustomerTableWithTestData(tableName, sortKeyVal)

      val deletionTarget = buildTableKeyDeletionTarget(tableName, DynamoDbTestConstants.SORT_KEY_NAME)
      val deletionKeyValue = DynamoDbDeletionKeyValue(
        primaryKeyValue = DynamoDbTestConstants.CUSTOMER_ID_1,
        secondaryKeyValue = sortKeyVal
      )
      DynamoDbDeletionConnector(dynamoDb).deleteData(deletionTarget, deletionKeyValue)

      validateDeleted(tableName, DynamoDbTestConstants.CUSTOMER_ID_1, sortKeyVal)
      validateNotDeleted(tableName, listOf(DynamoDbTestConstants.CUSTOMER_ID_2), sortKeyVal)
    }
  }

  private suspend fun createCustomerTableWithTestData(tableName: String, sortKeyVal: String? = null) {
    dynamoDbUtils.createTable(dynamoDb, tableName, sortKeyVal != null, null)
    Thread.sleep(DynamoDbTestConstants.WAIT_TIME_BETWEEN_DDB_OPERATIONS_MS)

    listOf(DynamoDbTestConstants.CUSTOMER_ID_1, DynamoDbTestConstants.CUSTOMER_ID_2).forEach {
      val itemKey = mutableMapOf(DynamoDbTestConstants.PARTITION_KEY_NAME to AttributeValue.S(it))
      if (sortKeyVal != null) {
        itemKey[DynamoDbTestConstants.SORT_KEY_NAME] = AttributeValue.S(sortKeyVal)
      }
      putItem(tableName, itemKey)
    }
  }

  private fun buildTableKeyDeletionTarget(tableName: String, sortKeyName: String?): DynamoDbDeletionTarget {
    val deletionKeySchema = DynamoDbDeletionKeySchema(
      primaryKeyName = DynamoDbTestConstants.PARTITION_KEY_NAME,
      secondaryKeyName = sortKeyName
    )

    return DynamoDbDeletionTarget(
      tableName = tableName,
      awsRegion = DynamoDbTestConstants.AWS_REGION,
      strategy = DynamoDbDeletionStrategyType.TABLE_KEY,
      partitionKeyName = DynamoDbTestConstants.PARTITION_KEY_NAME,
      sortKeyName = sortKeyName,
      deletionKeySchema = deletionKeySchema
    )
  }
}
