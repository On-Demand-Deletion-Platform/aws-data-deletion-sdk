package com.ondemanddeletionplatform.deletion.connectors.dynamodb.internal

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.DeleteItemRequest
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionStrategyType
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionTarget
import com.ondemanddeletionplatform.deletion.testutil.DynamoDbTestConstants
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions

class DynamoDbTableKeyDeletionStrategyTest {
  @Test
  fun invalidStrategy_throwsException() {
    val mockDdbClient: DynamoDbClient = mock()
    val exception = assertThrows(IllegalArgumentException::class.java) {
      runBlocking {
        DynamoDbTableKeyDeletionStrategy().deleteData(
          mockDdbClient,
          DynamoDbTestConstants.GSI_DELETION_TARGET,
          DynamoDbTestConstants.DELETION_KEY_VALUE
        )
      }
    }
    assertEquals("Deletion target strategy must be TABLE_KEY", exception.message)
    verifyNoInteractions(mockDdbClient)
  }

  @Test
  fun validDeletionKeyWithSortKey_deletesItem() {
    val mockDdbClient: DynamoDbClient = mock()
    runBlocking {
      DynamoDbTableKeyDeletionStrategy().deleteData(
        mockDdbClient,
        DynamoDbTestConstants.TABLE_KEY_DELETION_TARGET,
        DynamoDbTestConstants.DELETION_KEY_VALUE
      )
    }
    val expectedKey = mapOf(
      DynamoDbTestConstants.PARTITION_KEY_NAME to AttributeValue.S(
        DynamoDbTestConstants.CUSTOMER_ID
      ),
      DynamoDbTestConstants.SORT_KEY_NAME to AttributeValue.S(
        DynamoDbTestConstants.SORT_KEY_VALUE
      )
    )
    val expectedRequest = DeleteItemRequest {
      tableName = DynamoDbTestConstants.TABLE_NAME
      key = expectedKey
    }
    runBlocking {
      verify(mockDdbClient).deleteItem(expectedRequest)
    }
  }

  @Test
  fun validDeletionKeyWithoutSortKey_deletesItem() {
    val mockDdbClient: DynamoDbClient = mock()
    runBlocking {
      DynamoDbTableKeyDeletionStrategy().deleteData(
        mockDdbClient,
        DynamoDbDeletionTarget(
          strategy = DynamoDbDeletionStrategyType.TABLE_KEY,
          awsRegion = DynamoDbTestConstants.AWS_REGION,
          tableName = DynamoDbTestConstants.TABLE_NAME,
          partitionKeyName = DynamoDbTestConstants.PARTITION_KEY_NAME,
          deletionKeySchema = DynamoDbTestConstants.DELETION_KEY_SCHEMA_NO_SORT
        ),
        DynamoDbTestConstants.DELETION_KEY_VALUE_NO_SORT
      )
    }
    val expectedKey = mapOf(
      DynamoDbTestConstants.PARTITION_KEY_NAME to AttributeValue.S(
        DynamoDbTestConstants.CUSTOMER_ID
      )
    )
    val expectedRequest = DeleteItemRequest {
      tableName = DynamoDbTestConstants.TABLE_NAME
      key = expectedKey
    }
    runBlocking {
      verify(mockDdbClient).deleteItem(expectedRequest)
    }
  }
}
