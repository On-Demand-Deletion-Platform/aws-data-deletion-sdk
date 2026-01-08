package com.ondemanddeletionplatform.deletionworker.domain.connectors

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.DeleteItemRequest
import com.ondemanddeletionplatform.deletionworker.domain.models.DynamoDbDeletionKeySchema
import com.ondemanddeletionplatform.deletionworker.domain.models.DynamoDbDeletionKeyValue
import com.ondemanddeletionplatform.deletionworker.domain.models.DynamoDbDeletionStrategyType
import com.ondemanddeletionplatform.deletionworker.domain.models.DynamoDbDeletionTarget
import com.ondemanddeletionplatform.deletionworker.domain.models.ValidatedDynamoDbGsiDeletionTarget
import com.ondemanddeletionplatform.deletionworker.domain.models.ValidatedDynamoDbScanDeletionTarget
import com.ondemanddeletionplatform.deletionworker.domain.models.ValidatedDynamoDbTableKeyDeletionTarget
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class DynamoDbDeletionConnectorTest {
  companion object {
    private const val TEST_AWS_REGION = "us-west-2"
    private const val TEST_TABLE_NAME = "TestTable"
    private const val TEST_PARTITION_KEY_NAME = "CustomerId"
    private const val TEST_SORT_KEY_NAME = "SortKey"
    private const val TEST_GSI_NAME = "GsiIndex"
    private const val TEST_TABLE_DELETION_KEY_NAME = "DeletionKey"
    private const val TEST_CUSTOMER_ID = "Customer123"
    private const val TEST_SORT_KEY_VALUE = "SortValue456"
    private val TEST_DELETION_KEY_SCHEMA = DynamoDbDeletionKeySchema(
      primaryKeyName = TEST_PARTITION_KEY_NAME,
      secondaryKeyName = TEST_SORT_KEY_NAME
    )
    private val TEST_DELETION_KEY_VALUE = DynamoDbDeletionKeyValue(
      primaryKeyValue = TEST_CUSTOMER_ID,
      secondaryKeyValue = TEST_SORT_KEY_VALUE
    )
  }

  @Test
  fun deleteCustomerByGsiKey_invalidStrategy_throwsException() {
    val mockDdbClient = DynamoDbClient { region = TEST_AWS_REGION }
    val deletionConnector = DynamoDbDeletionConnector(mockDdbClient)

    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.GSI_QUERY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      deletionKeySchema = TEST_DELETION_KEY_SCHEMA
    )
    val exception = assertThrows(IllegalArgumentException::class.java) {
      runBlocking {
        deletionConnector.deleteData(deletionTarget, TEST_DELETION_KEY_VALUE)
      }
    }
    assertEquals("GSI name must not be null", exception.message)
  }

  @Test
  fun deleteCustomerByTableKey_successfulDeletion() {
    val mockDdbClient: DynamoDbClient = mock()
    val deletionConnector = DynamoDbDeletionConnector(mockDdbClient)

    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.TABLE_KEY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      sortKeyName = TEST_SORT_KEY_NAME,
      deletionKeySchema = TEST_DELETION_KEY_SCHEMA
    )

    val expectedDeleteItemRequest = DeleteItemRequest {
      tableName = TEST_TABLE_NAME
      key = mapOf(
        TEST_PARTITION_KEY_NAME to AttributeValue.S(TEST_CUSTOMER_ID),
        TEST_SORT_KEY_NAME to AttributeValue.S(TEST_SORT_KEY_VALUE)
      )
    }

    runBlocking {
      deletionConnector.deleteData(deletionTarget, TEST_DELETION_KEY_VALUE)

      verify(mockDdbClient).deleteItem(expectedDeleteItemRequest)
    }
  }

  @Test
  fun deleteCustomerByGsiKey_notYetImplemented() {
    val mockDdbClient = DynamoDbClient { region = TEST_AWS_REGION }
    val deletionConnector = DynamoDbDeletionConnector(mockDdbClient)

    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.GSI_QUERY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      gsiName = TEST_GSI_NAME,
      deletionKeySchema = TEST_DELETION_KEY_SCHEMA
    )

    assertThrows(NotImplementedError::class.java) {
      runBlocking {
        deletionConnector.deleteData(deletionTarget, TEST_DELETION_KEY_VALUE)
      }
    }
  }

  @Test
  fun deleteCustomerByScan_notYetImplemented() {
    val mockDdbClient = DynamoDbClient { region = TEST_AWS_REGION }
    val deletionConnector = DynamoDbDeletionConnector(mockDdbClient)

    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.SCAN,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      deletionKeySchema = TEST_DELETION_KEY_SCHEMA
    )

    assertThrows(NotImplementedError::class.java) {
      runBlocking {
        deletionConnector.deleteData(deletionTarget, TEST_DELETION_KEY_VALUE)
      }
    }
  }
}
