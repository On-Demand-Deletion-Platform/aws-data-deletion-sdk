package com.ondemanddeletionplatform.deletion.localinteg.connectors.dynamodb

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.ondemanddeletionplatform.deletion.connectors.dynamodb.DynamoDbDeletionConnector
import com.ondemanddeletionplatform.deletion.localinteg.testutil.dynamodb.DynamoDbIntegTestConstants
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionKeySchema
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionKeyValue
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionStrategyType
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionTarget
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Local integ tests for deleting DynamoDB data by scan.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DynamoDbDeleteByScanKeyIntegTest : DynamoDbIntegTest() {
  @Tag("localIntegTest")
  @Test
  fun scanDeletionStrategyCanDeleteWithoutSortKey() {
    val tableName = "TestDeleteByScanWithoutSortKey"
    val scanAttributeName = "scanAttribute"
    val scanAttributeValToDelete = "scanAttributeValToDelete"
    val otherScanAttributeVal = "otherScanAttributeVal"

    runBlocking {
      createCustomerTable(tableName, false)

      // Customer 1 matches scan attribute
      putItem(
        tableName,
        mapOf(
          DynamoDbIntegTestConstants.PARTITION_KEY_NAME to AttributeValue.S(DynamoDbIntegTestConstants.CUSTOMER_ID_1),
          scanAttributeName to AttributeValue.S(scanAttributeValToDelete)
        )
      )
      // Customer 2 doesn't match scan attribute
      putItem(
        tableName,
        mapOf(
          DynamoDbIntegTestConstants.PARTITION_KEY_NAME to AttributeValue.S(DynamoDbIntegTestConstants.CUSTOMER_ID_2),
          scanAttributeName to AttributeValue.S(otherScanAttributeVal)
        )
      )
      // Customer 3 matches scan attribute
      putItem(
        tableName,
        mapOf(
          DynamoDbIntegTestConstants.PARTITION_KEY_NAME to AttributeValue.S(DynamoDbIntegTestConstants.CUSTOMER_ID_3),
          scanAttributeName to AttributeValue.S(scanAttributeValToDelete)
        )
      )
      // Customer 4 has no scan attribute
      putItem(
        tableName,
        mapOf(
          DynamoDbIntegTestConstants.PARTITION_KEY_NAME to AttributeValue.S(DynamoDbIntegTestConstants.CUSTOMER_ID_4)
        )
      )
      println("Populated test data")

      val deletionTarget = buildScanDeletionTarget(tableName, null, scanAttributeName, null)
      val deletionKeyValue = DynamoDbDeletionKeyValue(
        primaryKeyValue = scanAttributeValToDelete
      )
      DynamoDbDeletionConnector(dynamoDb).deleteData(deletionTarget, deletionKeyValue)

      validateDeleted(tableName, DynamoDbIntegTestConstants.CUSTOMER_ID_1, null)
      validateDeleted(tableName, DynamoDbIntegTestConstants.CUSTOMER_ID_3, null)

      val nonMatchingCustomerIds = listOf(DynamoDbIntegTestConstants.CUSTOMER_ID_2, DynamoDbIntegTestConstants.CUSTOMER_ID_4)
      validateNotDeleted(tableName, nonMatchingCustomerIds, null)
    }
  }

  @Suppress("LongMethod")
  @Tag("localIntegTest")
  @Test
  fun scanDeletionStrategyCanDeleteWithSortKey() {
    val tableName = "TestDeleteByScanWithSortKey"
    val sortKeyName = DynamoDbIntegTestConstants.SORT_KEY_NAME
    val scanPrimaryAttributeName = "scanPrimaryAttribute"
    val scanSecondaryAttributeName = "scanSecondaryAttribute"
    val scanPrimaryAttributeValToDelete = "scanPrimaryAttributeValToDelete"
    val otherScanPrimaryAttributeVal = "otherScanPrimaryAttributeVal"
    val scanSecondaryAttributeValToDelete = "scanSecondaryAttributeValToDelete"
    val otherScanSecondaryAttributeVal = "otherScanSecondaryAttributeVal"

    runBlocking {
      createCustomerTable(tableName, true)

      // Customer 1 matches full scan deletion key
      putItem(
        tableName,
        mapOf(
          DynamoDbIntegTestConstants.PARTITION_KEY_NAME to AttributeValue.S(DynamoDbIntegTestConstants.CUSTOMER_ID_1),
          DynamoDbIntegTestConstants.SORT_KEY_NAME to AttributeValue.S(DynamoDbIntegTestConstants.SORT_KEY_VALUE),
          scanPrimaryAttributeName to AttributeValue.S(scanPrimaryAttributeValToDelete),
          scanSecondaryAttributeName to AttributeValue.S(scanSecondaryAttributeValToDelete)
        )
      )
      // Customer 2 doesn't match scan primary attribute
      putItem(
        tableName,
        mapOf(
          DynamoDbIntegTestConstants.PARTITION_KEY_NAME to AttributeValue.S(DynamoDbIntegTestConstants.CUSTOMER_ID_2),
          DynamoDbIntegTestConstants.SORT_KEY_NAME to AttributeValue.S(DynamoDbIntegTestConstants.SORT_KEY_VALUE),
          scanPrimaryAttributeName to AttributeValue.S(otherScanPrimaryAttributeVal),
          scanSecondaryAttributeName to AttributeValue.S(scanSecondaryAttributeValToDelete)
        )
      )
      // Customer 3 doesn't match scan secondary attribute
      putItem(
        tableName,
        mapOf(
          DynamoDbIntegTestConstants.PARTITION_KEY_NAME to AttributeValue.S(DynamoDbIntegTestConstants.CUSTOMER_ID_3),
          DynamoDbIntegTestConstants.SORT_KEY_NAME to AttributeValue.S(DynamoDbIntegTestConstants.SORT_KEY_VALUE),
          scanPrimaryAttributeName to AttributeValue.S(scanPrimaryAttributeValToDelete),
          scanSecondaryAttributeName to AttributeValue.S(otherScanSecondaryAttributeVal)
        )
      )
      // Customer 4 matches full scan deletion key
      putItem(
        tableName,
        mapOf(
          DynamoDbIntegTestConstants.PARTITION_KEY_NAME to AttributeValue.S(DynamoDbIntegTestConstants.CUSTOMER_ID_4),
          DynamoDbIntegTestConstants.SORT_KEY_NAME to AttributeValue.S(DynamoDbIntegTestConstants.SORT_KEY_VALUE_2),
          scanPrimaryAttributeName to AttributeValue.S(scanPrimaryAttributeValToDelete),
          scanSecondaryAttributeName to AttributeValue.S(scanSecondaryAttributeValToDelete)
        )
      )
      // Customer 5 has no scan primary attribute
      putItem(
        tableName,
        mapOf(
          DynamoDbIntegTestConstants.PARTITION_KEY_NAME to AttributeValue.S(DynamoDbIntegTestConstants.CUSTOMER_ID_5),
          DynamoDbIntegTestConstants.SORT_KEY_NAME to AttributeValue.S(DynamoDbIntegTestConstants.SORT_KEY_VALUE),
          scanSecondaryAttributeName to AttributeValue.S(scanSecondaryAttributeValToDelete)
        )
      )
      println("Populated test data")

      val deletionTarget = buildScanDeletionTarget(tableName, sortKeyName, scanPrimaryAttributeName, scanSecondaryAttributeName)
      val deletionKeyValue = DynamoDbDeletionKeyValue(
        primaryKeyValue = scanPrimaryAttributeValToDelete,
        secondaryKeyValue = scanSecondaryAttributeValToDelete
      )
      DynamoDbDeletionConnector(dynamoDb).deleteData(deletionTarget, deletionKeyValue)

      validateDeleted(tableName, DynamoDbIntegTestConstants.CUSTOMER_ID_1, DynamoDbIntegTestConstants.SORT_KEY_VALUE)
      validateDeleted(tableName, DynamoDbIntegTestConstants.CUSTOMER_ID_4, DynamoDbIntegTestConstants.SORT_KEY_VALUE_2)

      val nonMatchingCustomerIds = listOf(
        DynamoDbIntegTestConstants.CUSTOMER_ID_2,
        DynamoDbIntegTestConstants.CUSTOMER_ID_3,
        DynamoDbIntegTestConstants.CUSTOMER_ID_5
      )
      validateNotDeleted(tableName, nonMatchingCustomerIds, DynamoDbIntegTestConstants.SORT_KEY_VALUE)
    }
  }

  private suspend fun createCustomerTable(tableName: String, withSortKey: Boolean) {
    dynamoDbUtils.createTable(dynamoDb, tableName, withSortKey, null)
    Thread.sleep(DynamoDbIntegTestConstants.WAIT_TIME_BETWEEN_DDB_OPERATIONS_MS)
  }

  private fun buildScanDeletionTarget(
    tableName: String,
    sortKeyName: String?,
    scanPrimaryAttributeName: String,
    scanSecondaryAttributeName: String?
  ): DynamoDbDeletionTarget {
    return DynamoDbDeletionTarget(
      tableName = tableName,
      awsRegion = DynamoDbIntegTestConstants.AWS_REGION,
      strategy = DynamoDbDeletionStrategyType.SCAN,
      partitionKeyName = DynamoDbIntegTestConstants.PARTITION_KEY_NAME,
      sortKeyName = sortKeyName,
      deletionKeySchema = DynamoDbDeletionKeySchema(
        primaryKeyName = scanPrimaryAttributeName,
        secondaryKeyName = scanSecondaryAttributeName
      )
    )
  }
}
