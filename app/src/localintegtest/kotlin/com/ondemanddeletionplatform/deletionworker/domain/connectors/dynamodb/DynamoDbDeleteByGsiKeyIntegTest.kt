package com.ondemanddeletionplatform.deletionworker.domain.connectors.dynamodb

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.GetItemRequest
import aws.sdk.kotlin.services.dynamodb.model.GetItemResponse
import aws.sdk.kotlin.services.dynamodb.model.PutItemRequest
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.DynamoDbDeletionKeySchema
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.DynamoDbDeletionKeyValue
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.DynamoDbDeletionStrategyType
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.DynamoDbDeletionTarget
import com.ondemanddeletionplatform.deletionworker.testutil.dynamodb.DynamoDbIntegTestConstants
import com.ondemanddeletionplatform.deletionworker.testutil.dynamodb.DynamoDbLocalStack
import com.ondemanddeletionplatform.deletionworker.testutil.dynamodb.DynamoDbRepositoryUtils
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Local integ tests for deleting DynamoDB data by GSI key.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DynamoDbDeleteByGsiKeyIntegTest {
  private lateinit var localstack: DynamoDbLocalStack
  private lateinit var dynamoDb: DynamoDbClient
  private val dynamoDbUtils = DynamoDbRepositoryUtils()

  @BeforeAll
  fun setupLocalStack() {
    localstack = DynamoDbLocalStack()
    dynamoDb = localstack.dynamoDb
  }

  @Suppress("LongMethod")
  @Tag("localIntegTest")
  @Test
  fun gsiKeyDeletionStrategyCanDeleteWithSortKey() {
    val tableName = "TestDeleteByGsiKeyWithSortKey"
    val sortKeyName = DynamoDbIntegTestConstants.SORT_KEY_NAME
    val gsiPartitionKeyValToDelete = "gsiPartitionKeyValToDelete"
    val otherGsiPartitionKeyVal = "otherGsiPartitionKeyVal"
    val gsiSortKeyValToDelete = "gsiSortKeyValToDelete"
    val otherGsiSortKeyVal = "otherGsiSortKeyVal"

    runBlocking {
      createCustomerTable(tableName, true, DynamoDbIntegTestConstants.GSI_PARTITION_KEY_NAME, DynamoDbIntegTestConstants.GSI_SORT_KEY_NAME)

      // Customer 1 matches full GSI deletion key
      putItem(
        tableName,
        mapOf(
          DynamoDbIntegTestConstants.PARTITION_KEY_NAME to AttributeValue.S(DynamoDbIntegTestConstants.CUSTOMER_ID_1),
          DynamoDbIntegTestConstants.SORT_KEY_NAME to AttributeValue.S(DynamoDbIntegTestConstants.SORT_KEY_VALUE),
          DynamoDbIntegTestConstants.GSI_PARTITION_KEY_NAME to AttributeValue.S(gsiPartitionKeyValToDelete),
          DynamoDbIntegTestConstants.GSI_SORT_KEY_NAME to AttributeValue.S(gsiSortKeyValToDelete)
        )
      )
      // Customer 2 doesn't match GSI partition key
      putItem(
        tableName,
        mapOf(
          DynamoDbIntegTestConstants.PARTITION_KEY_NAME to AttributeValue.S(DynamoDbIntegTestConstants.CUSTOMER_ID_2),
          DynamoDbIntegTestConstants.SORT_KEY_NAME to AttributeValue.S(DynamoDbIntegTestConstants.SORT_KEY_VALUE),
          DynamoDbIntegTestConstants.GSI_PARTITION_KEY_NAME to AttributeValue.S(otherGsiPartitionKeyVal),
          DynamoDbIntegTestConstants.GSI_SORT_KEY_NAME to AttributeValue.S(gsiSortKeyValToDelete)
        )
      )
      // Customer 3 doesn't match GSI sort key
      putItem(
        tableName,
        mapOf(
          DynamoDbIntegTestConstants.PARTITION_KEY_NAME to AttributeValue.S(DynamoDbIntegTestConstants.CUSTOMER_ID_3),
          DynamoDbIntegTestConstants.SORT_KEY_NAME to AttributeValue.S(DynamoDbIntegTestConstants.SORT_KEY_VALUE),
          DynamoDbIntegTestConstants.GSI_PARTITION_KEY_NAME to AttributeValue.S(gsiPartitionKeyValToDelete),
          DynamoDbIntegTestConstants.GSI_SORT_KEY_NAME to AttributeValue.S(otherGsiSortKeyVal)
        )
      )
      // Customer 4 matches full GSI sort key
      putItem(
        tableName,
        mapOf(
          DynamoDbIntegTestConstants.PARTITION_KEY_NAME to AttributeValue.S(DynamoDbIntegTestConstants.CUSTOMER_ID_4),
          DynamoDbIntegTestConstants.SORT_KEY_NAME to AttributeValue.S(DynamoDbIntegTestConstants.SORT_KEY_VALUE_2),
          DynamoDbIntegTestConstants.GSI_PARTITION_KEY_NAME to AttributeValue.S(gsiPartitionKeyValToDelete),
          DynamoDbIntegTestConstants.GSI_SORT_KEY_NAME to AttributeValue.S(gsiSortKeyValToDelete)
        )
      )
      // Customer 5 has no GSI partition key
      putItem(
        tableName,
        mapOf(
          DynamoDbIntegTestConstants.PARTITION_KEY_NAME to AttributeValue.S(DynamoDbIntegTestConstants.CUSTOMER_ID_5),
          DynamoDbIntegTestConstants.SORT_KEY_NAME to AttributeValue.S(DynamoDbIntegTestConstants.SORT_KEY_VALUE),
          DynamoDbIntegTestConstants.GSI_SORT_KEY_NAME to AttributeValue.S(gsiSortKeyValToDelete)
        )
      )
      println("Populated test data")

      val deletionTarget = buildGsiKeyDeletionTarget(tableName, sortKeyName, DynamoDbIntegTestConstants.GSI_SORT_KEY_NAME)
      val deletionKeyValue = DynamoDbDeletionKeyValue(
        primaryKeyValue = gsiPartitionKeyValToDelete,
        secondaryKeyValue = gsiSortKeyValToDelete
      )
      DynamoDbDeletionConnector(dynamoDb).deleteData(deletionTarget, deletionKeyValue)

      // Validate deleted expected items
      val getItemResultCustomer1 = getCustomerItem(
        tableName,
        DynamoDbIntegTestConstants.CUSTOMER_ID_1,
        DynamoDbIntegTestConstants.SORT_KEY_VALUE
      )
      assertNull(getItemResultCustomer1.item)
      val getItemResultCustomer4 = getCustomerItem(
        tableName,
        DynamoDbIntegTestConstants.CUSTOMER_ID_4,
        DynamoDbIntegTestConstants.SORT_KEY_VALUE_2
      )
      assertNull(getItemResultCustomer4.item)
      // Validate did not delete items not matching either deletion target's GSI partition key or GSI sort key
      listOf(
        DynamoDbIntegTestConstants.CUSTOMER_ID_2,
        DynamoDbIntegTestConstants.CUSTOMER_ID_3,
        DynamoDbIntegTestConstants.CUSTOMER_ID_5
      ).forEach {
        val getItemResult = getCustomerItem(tableName, it, DynamoDbIntegTestConstants.SORT_KEY_VALUE)
        validateNonNullGetItemResponse(getItemResult, it)
      }
    }
  }

  private suspend fun createCustomerTable(tableName: String, withSortKey: Boolean, gsiPartitionKeyName: String, gsiSortKeyName: String?) {
    val gsis = listOf(dynamoDbUtils.buildGlobalSecondaryIndexModel(gsiPartitionKeyName, gsiSortKeyName))
    dynamoDbUtils.createTable(dynamoDb, tableName, withSortKey, gsis)
    Thread.sleep(DynamoDbIntegTestConstants.WAIT_TIME_BETWEEN_DDB_OPERATIONS_MS)
  }

  private suspend fun putItem(tableName: String, item: Map<String, AttributeValue>) {
    dynamoDb.putItem(
      PutItemRequest {
        this.tableName = tableName
        this.item = item
      }
    )
    println("Put item into table $tableName with key $item")
    Thread.sleep(DynamoDbIntegTestConstants.WAIT_TIME_BETWEEN_DDB_OPERATIONS_MS)
  }

  private suspend fun getCustomerItem(tableName: String, customerId: String, sortKeyVal: String?): GetItemResponse {
    val tableKey = buildItemKey(customerId, sortKeyVal)
    val getItemResponse = dynamoDb.getItem(
      GetItemRequest {
        this.tableName = tableName
        key = tableKey
      }
    )
    println("GetItem(tableName: $tableName, partitionKey: $customerId, sortKey: $sortKeyVal): $getItemResponse")
    return getItemResponse
  }

  private fun buildItemKey(partitionKeyVal: String, sortKeyVal: String?): Map<String, AttributeValue> {
    val keyValueMap = mutableMapOf(DynamoDbIntegTestConstants.PARTITION_KEY_NAME to AttributeValue.S(partitionKeyVal))
    if (sortKeyVal != null) {
      keyValueMap[DynamoDbIntegTestConstants.SORT_KEY_NAME] = AttributeValue.S(sortKeyVal)
    }
    return keyValueMap
  }

  private fun buildGsiKeyDeletionTarget(tableName: String, sortKeyName: String?, gsiSortKeyName: String?): DynamoDbDeletionTarget {
    val deletionKeySchema = buildDeletionKeySchema(gsiSortKeyName)

    return DynamoDbDeletionTarget(
      tableName = tableName,
      awsRegion = DynamoDbIntegTestConstants.AWS_REGION,
      strategy = DynamoDbDeletionStrategyType.GSI_QUERY,
      partitionKeyName = DynamoDbIntegTestConstants.PARTITION_KEY_NAME,
      sortKeyName = sortKeyName,
      gsiName = DynamoDbIntegTestConstants.GSI_NAME,
      deletionKeySchema = deletionKeySchema
    )
  }

  private fun buildDeletionKeySchema(gsiSortKeyName: String?): DynamoDbDeletionKeySchema {
    return DynamoDbDeletionKeySchema(
      primaryKeyName = DynamoDbIntegTestConstants.GSI_PARTITION_KEY_NAME,
      secondaryKeyName = gsiSortKeyName
    )
  }

  private fun validateNonNullGetItemResponse(response: GetItemResponse, expectedPartitionKeyVal: String) {
    val ddbItem: Map<String, AttributeValue>? = response.item
    assertNotNull(ddbItem)
    assertEquals(
      ddbItem?.get(DynamoDbIntegTestConstants.PARTITION_KEY_NAME),
      AttributeValue.S(expectedPartitionKeyVal)
    )
  }
}
