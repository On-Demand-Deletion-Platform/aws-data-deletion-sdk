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
 * Local integ tests for deleting DynamoDB data by GSI key.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DynamoDbDeleteByGsiKeyIntegTest : DynamoDbIntegTest() {
  @Tag("localIntegTest")
  @Test
  fun gsiKeyDeletionStrategyCanDeleteWithoutSortKey() {
    val tableName = "TestDeleteByGsiKeyWithoutSortKey"
    val gsiPartitionKeyValToDelete = "gsiPartitionKeyValToDelete"
    val otherGsiPartitionKeyVal = "otherGsiPartitionKeyVal"

    runBlocking {
      createCustomerTable(tableName, true, DynamoDbTestConstants.TEST_GSI_PARTITION_KEY_NAME, null)

      // Customer 1 matches GSI partition key
      putItem(
        tableName,
        mapOf(
          DynamoDbTestConstants.TEST_PARTITION_KEY_NAME to AttributeValue.S(DynamoDbTestConstants.TEST_CUSTOMER_ID_1),
          DynamoDbTestConstants.TEST_SORT_KEY_NAME to AttributeValue.S(DynamoDbTestConstants.TEST_SORT_KEY_VALUE),
          DynamoDbTestConstants.TEST_GSI_PARTITION_KEY_NAME to AttributeValue.S(gsiPartitionKeyValToDelete)
        )
      )
      // Customer 2 doesn't match GSI partition key
      putItem(
        tableName,
        mapOf(
          DynamoDbTestConstants.TEST_PARTITION_KEY_NAME to AttributeValue.S(DynamoDbTestConstants.TEST_CUSTOMER_ID_2),
          DynamoDbTestConstants.TEST_SORT_KEY_NAME to AttributeValue.S(DynamoDbTestConstants.TEST_SORT_KEY_VALUE),
          DynamoDbTestConstants.TEST_GSI_PARTITION_KEY_NAME to AttributeValue.S(otherGsiPartitionKeyVal)
        )
      )
      // Customer 3 matches GSI partition key
      putItem(
        tableName,
        mapOf(
          DynamoDbTestConstants.TEST_PARTITION_KEY_NAME to AttributeValue.S(DynamoDbTestConstants.TEST_CUSTOMER_ID_3),
          DynamoDbTestConstants.TEST_SORT_KEY_NAME to AttributeValue.S(DynamoDbTestConstants.TEST_SORT_KEY_VALUE),
          DynamoDbTestConstants.TEST_GSI_PARTITION_KEY_NAME to AttributeValue.S(gsiPartitionKeyValToDelete)
        )
      )
      // Customer 4 has no GSI partition key
      putItem(
        tableName,
        mapOf(
          DynamoDbTestConstants.TEST_PARTITION_KEY_NAME to AttributeValue.S(DynamoDbTestConstants.TEST_CUSTOMER_ID_4),
          DynamoDbTestConstants.TEST_SORT_KEY_NAME to AttributeValue.S(DynamoDbTestConstants.TEST_SORT_KEY_VALUE)
        )
      )
      println("Populated test data")

      val deletionTarget = buildGsiKeyDeletionTarget(tableName, DynamoDbTestConstants.TEST_SORT_KEY_NAME, null)
      val deletionKeyValue = DynamoDbDeletionKeyValue(
        primaryKeyValue = gsiPartitionKeyValToDelete,
        secondaryKeyValue = null
      )
      DynamoDbDeletionConnector(dynamoDb).deleteData(deletionTarget, deletionKeyValue)

      validateDeleted(tableName, DynamoDbTestConstants.TEST_CUSTOMER_ID_1, DynamoDbTestConstants.TEST_SORT_KEY_VALUE)
      validateDeleted(tableName, DynamoDbTestConstants.TEST_CUSTOMER_ID_3, DynamoDbTestConstants.TEST_SORT_KEY_VALUE)

      val nonMatchingCustomerIds = listOf(DynamoDbTestConstants.TEST_CUSTOMER_ID_2, DynamoDbTestConstants.TEST_CUSTOMER_ID_4)
      validateNotDeleted(tableName, nonMatchingCustomerIds, DynamoDbTestConstants.TEST_SORT_KEY_VALUE)
    }
  }

  @Suppress("LongMethod")
  @Tag("localIntegTest")
  @Test
  fun gsiKeyDeletionStrategyCanDeleteWithSortKey() {
    val tableName = "TestDeleteByGsiKeyWithSortKey"
    val sortKeyName = DynamoDbTestConstants.TEST_SORT_KEY_NAME
    val gsiPartitionKeyValToDelete = "gsiPartitionKeyValToDelete"
    val otherGsiPartitionKeyVal = "otherGsiPartitionKeyVal"
    val gsiSortKeyValToDelete = "gsiSortKeyValToDelete"
    val otherGsiSortKeyVal = "otherGsiSortKeyVal"

    runBlocking {
      createCustomerTable(tableName, true, DynamoDbTestConstants.TEST_GSI_PARTITION_KEY_NAME, DynamoDbTestConstants.TEST_GSI_SORT_KEY_NAME)

      // Customer 1 matches full GSI deletion key
      putItem(
        tableName,
        mapOf(
          DynamoDbTestConstants.TEST_PARTITION_KEY_NAME to AttributeValue.S(DynamoDbTestConstants.TEST_CUSTOMER_ID_1),
          DynamoDbTestConstants.TEST_SORT_KEY_NAME to AttributeValue.S(DynamoDbTestConstants.TEST_SORT_KEY_VALUE),
          DynamoDbTestConstants.TEST_GSI_PARTITION_KEY_NAME to AttributeValue.S(gsiPartitionKeyValToDelete),
          DynamoDbTestConstants.TEST_GSI_SORT_KEY_NAME to AttributeValue.S(gsiSortKeyValToDelete)
        )
      )
      // Customer 2 doesn't match GSI partition key
      putItem(
        tableName,
        mapOf(
          DynamoDbTestConstants.TEST_PARTITION_KEY_NAME to AttributeValue.S(DynamoDbTestConstants.TEST_CUSTOMER_ID_2),
          DynamoDbTestConstants.TEST_SORT_KEY_NAME to AttributeValue.S(DynamoDbTestConstants.TEST_SORT_KEY_VALUE),
          DynamoDbTestConstants.TEST_GSI_PARTITION_KEY_NAME to AttributeValue.S(otherGsiPartitionKeyVal),
          DynamoDbTestConstants.TEST_GSI_SORT_KEY_NAME to AttributeValue.S(gsiSortKeyValToDelete)
        )
      )
      // Customer 3 doesn't match GSI sort key
      putItem(
        tableName,
        mapOf(
          DynamoDbTestConstants.TEST_PARTITION_KEY_NAME to AttributeValue.S(DynamoDbTestConstants.TEST_CUSTOMER_ID_3),
          DynamoDbTestConstants.TEST_SORT_KEY_NAME to AttributeValue.S(DynamoDbTestConstants.TEST_SORT_KEY_VALUE),
          DynamoDbTestConstants.TEST_GSI_PARTITION_KEY_NAME to AttributeValue.S(gsiPartitionKeyValToDelete),
          DynamoDbTestConstants.TEST_GSI_SORT_KEY_NAME to AttributeValue.S(otherGsiSortKeyVal)
        )
      )
      // Customer 4 matches full GSI sort key
      putItem(
        tableName,
        mapOf(
          DynamoDbTestConstants.TEST_PARTITION_KEY_NAME to AttributeValue.S(DynamoDbTestConstants.TEST_CUSTOMER_ID_4),
          DynamoDbTestConstants.TEST_SORT_KEY_NAME to AttributeValue.S(DynamoDbTestConstants.TEST_SORT_KEY_VALUE_2),
          DynamoDbTestConstants.TEST_GSI_PARTITION_KEY_NAME to AttributeValue.S(gsiPartitionKeyValToDelete),
          DynamoDbTestConstants.TEST_GSI_SORT_KEY_NAME to AttributeValue.S(gsiSortKeyValToDelete)
        )
      )
      // Customer 5 has no GSI partition key
      putItem(
        tableName,
        mapOf(
          DynamoDbTestConstants.TEST_PARTITION_KEY_NAME to AttributeValue.S(DynamoDbTestConstants.TEST_CUSTOMER_ID_5),
          DynamoDbTestConstants.TEST_SORT_KEY_NAME to AttributeValue.S(DynamoDbTestConstants.TEST_SORT_KEY_VALUE),
          DynamoDbTestConstants.TEST_GSI_SORT_KEY_NAME to AttributeValue.S(gsiSortKeyValToDelete)
        )
      )
      println("Populated test data")

      val deletionTarget = buildGsiKeyDeletionTarget(tableName, sortKeyName, DynamoDbTestConstants.TEST_GSI_SORT_KEY_NAME)
      val deletionKeyValue = DynamoDbDeletionKeyValue(
        primaryKeyValue = gsiPartitionKeyValToDelete,
        secondaryKeyValue = gsiSortKeyValToDelete
      )
      DynamoDbDeletionConnector(dynamoDb).deleteData(deletionTarget, deletionKeyValue)

      validateDeleted(tableName, DynamoDbTestConstants.TEST_CUSTOMER_ID_1, DynamoDbTestConstants.TEST_SORT_KEY_VALUE)
      validateDeleted(tableName, DynamoDbTestConstants.TEST_CUSTOMER_ID_4, DynamoDbTestConstants.TEST_SORT_KEY_VALUE_2)

      val nonMatchingCustomerIds = listOf(
        DynamoDbTestConstants.TEST_CUSTOMER_ID_2,
        DynamoDbTestConstants.TEST_CUSTOMER_ID_3,
        DynamoDbTestConstants.TEST_CUSTOMER_ID_5
      )
      validateNotDeleted(tableName, nonMatchingCustomerIds, DynamoDbTestConstants.TEST_SORT_KEY_VALUE)
    }
  }

  private suspend fun createCustomerTable(tableName: String, withSortKey: Boolean, gsiPartitionKeyName: String, gsiSortKeyName: String?) {
    val gsis = listOf(dynamoDbUtils.buildGlobalSecondaryIndexModel(gsiPartitionKeyName, gsiSortKeyName))
    dynamoDbUtils.createTable(dynamoDb, tableName, withSortKey, gsis)
    Thread.sleep(DynamoDbTestConstants.TEST_WAIT_TIME_BETWEEN_DDB_OPERATIONS_MS)
  }

  private fun buildGsiKeyDeletionTarget(tableName: String, sortKeyName: String?, gsiSortKeyName: String?): DynamoDbDeletionTarget {
    val deletionKeySchema = buildDeletionKeySchema(gsiSortKeyName)

    return DynamoDbDeletionTarget(
      tableName = tableName,
      awsRegion = DynamoDbTestConstants.TEST_AWS_REGION,
      strategy = DynamoDbDeletionStrategyType.GSI_QUERY,
      partitionKeyName = DynamoDbTestConstants.TEST_PARTITION_KEY_NAME,
      sortKeyName = sortKeyName,
      gsiName = DynamoDbTestConstants.TEST_GSI_NAME,
      deletionKeySchema = deletionKeySchema
    )
  }

  private fun buildDeletionKeySchema(gsiSortKeyName: String?): DynamoDbDeletionKeySchema {
    return DynamoDbDeletionKeySchema(
      primaryKeyName = DynamoDbTestConstants.TEST_GSI_PARTITION_KEY_NAME,
      secondaryKeyName = gsiSortKeyName
    )
  }
}
