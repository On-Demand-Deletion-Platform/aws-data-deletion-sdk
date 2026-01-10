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
 * Local DynamoDB deletion connector integ tests using LocalStack.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DynamoDbDeleteByTableKeyIntegTest {
  private lateinit var localstack: DynamoDbLocalStack
  private lateinit var dynamoDb: DynamoDbClient
  private val dynamoDbUtils = DynamoDbRepositoryUtils()

  @BeforeAll
  fun setupLocalStack() {
    localstack = DynamoDbLocalStack()
    dynamoDb = localstack.dynamoDb
  }

  @Tag("localIntegTest")
  @Test
  fun tableKeyDeletionStrategyCanDeleteWithoutSortKey() {
    val tableName = "TestDeleteByPartitionKeyWithoutSortKey"
    runBlocking {
      createCustomerTableWithTestData(tableName)

      val deletionTarget = buildTableKeyDeletionTarget(tableName, null)
      val deletionKeyValue = DynamoDbDeletionKeyValue(
        primaryKeyValue = DynamoDbIntegTestConstants.CUSTOMER_ID_1
      )
      DynamoDbDeletionConnector(dynamoDb).deleteData(deletionTarget, deletionKeyValue)
      val getItemResult1 = getCustomerItem(tableName, DynamoDbIntegTestConstants.CUSTOMER_ID_1)
      assertNull(getItemResult1.item)
      val getItemResult2 = getCustomerItem(tableName, DynamoDbIntegTestConstants.CUSTOMER_ID_2)
      validateNonNullGetItemResponse(getItemResult2, DynamoDbIntegTestConstants.CUSTOMER_ID_2)
    }
  }

  @Tag("localIntegTest")
  @Test
  fun tableKeyDeletionStrategyCanDeleteWithSortKey() {
    val tableName = "TestDeleteByPartitionKeyWithSortKey"
    val sortKeyVal = DynamoDbIntegTestConstants.SORT_KEY_VALUE
    runBlocking {
      createCustomerTableWithTestData(tableName, sortKeyVal)

      val deletionTarget = buildTableKeyDeletionTarget(tableName, DynamoDbIntegTestConstants.SORT_KEY_NAME)
      val deletionKeyValue = DynamoDbDeletionKeyValue(
        primaryKeyValue = DynamoDbIntegTestConstants.CUSTOMER_ID_1,
        secondaryKeyValue = sortKeyVal
      )
      DynamoDbDeletionConnector(dynamoDb).deleteData(deletionTarget, deletionKeyValue)
      val getItemResult1 = getCustomerItem(tableName, DynamoDbIntegTestConstants.CUSTOMER_ID_1, sortKeyVal)
      assertNull(getItemResult1.item)
      val getItemResult2 = getCustomerItem(tableName, DynamoDbIntegTestConstants.CUSTOMER_ID_2, sortKeyVal)
      validateNonNullGetItemResponse(getItemResult2, DynamoDbIntegTestConstants.CUSTOMER_ID_2)
    }
  }

  private suspend fun createCustomerTableWithTestData(tableName: String, sortKeyVal: String? = null) {
    dynamoDbUtils.createTable(dynamoDb, tableName, sortKeyVal != null)
    Thread.sleep(DynamoDbIntegTestConstants.WAIT_TIME_BETWEEN_DDB_OPERATIONS_MS)

    putCustomerItem(tableName, DynamoDbIntegTestConstants.CUSTOMER_ID_1, sortKeyVal)
    Thread.sleep(DynamoDbIntegTestConstants.WAIT_TIME_BETWEEN_DDB_OPERATIONS_MS)
    putCustomerItem(tableName, DynamoDbIntegTestConstants.CUSTOMER_ID_2, sortKeyVal)
    println("Populated test data")
  }

  private suspend fun putCustomerItem(tableName: String, customerId: String, sortKeyVal: String? = null) {
    val customerItem = buildItemKey(customerId, sortKeyVal)
    dynamoDb.putItem(
      PutItemRequest {
        this.tableName = tableName
        item = customerItem
      }
    )
    println("Put item into table $tableName with key $customerItem")
  }

  private suspend fun getCustomerItem(tableName: String, customerId: String, sortKeyVal: String? = null): GetItemResponse {
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

  private fun buildTableKeyDeletionTarget(tableName: String, sortKeyName: String?): DynamoDbDeletionTarget {
    val deletionKeySchema = buildDeletionKeySchema(sortKeyName)

    return DynamoDbDeletionTarget(
      tableName = tableName,
      awsRegion = DynamoDbIntegTestConstants.AWS_REGION,
      strategy = DynamoDbDeletionStrategyType.TABLE_KEY,
      partitionKeyName = DynamoDbIntegTestConstants.PARTITION_KEY_NAME,
      sortKeyName = sortKeyName,
      deletionKeySchema = deletionKeySchema
    )
  }

  private fun buildDeletionKeySchema(sortKeyName: String?): DynamoDbDeletionKeySchema {
    return DynamoDbDeletionKeySchema(
      primaryKeyName = DynamoDbIntegTestConstants.PARTITION_KEY_NAME,
      secondaryKeyName = sortKeyName
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
