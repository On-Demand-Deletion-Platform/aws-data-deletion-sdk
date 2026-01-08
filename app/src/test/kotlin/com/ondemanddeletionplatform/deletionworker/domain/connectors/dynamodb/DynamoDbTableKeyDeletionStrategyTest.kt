package com.ondemanddeletionplatform.deletionworker.domain.connectors.dynamodb

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.DeleteItemRequest
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.DynamoDbDeletionKeySchema
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.DynamoDbDeletionKeyValue
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.DynamoDbDeletionStrategyType
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.DynamoDbDeletionTarget
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.ValidatedDynamoDbGsiDeletionTarget
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.ValidatedDynamoDbScanDeletionTarget
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.ValidatedDynamoDbTableKeyDeletionTarget
import com.ondemanddeletionplatform.deletionworker.testutil.DynamoDbTestConstants
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions

class DynamoDbTableKeyDeletionStrategyTest {
  @Test
  fun invalidStrategy_throwsException() {
    val mockDdbClient: DynamoDbClient = mock()
    val exception = assertThrows(IllegalArgumentException::class.java) {
      runBlocking {
        DynamoDbTableKeyDeletionStrategy().deleteData(mockDdbClient, DynamoDbTestConstants.TEST_GSI_DELETION_TARGET, DynamoDbTestConstants.TEST_DELETION_KEY_VALUE)
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
        DynamoDbTestConstants.TEST_TABLE_KEY_DELETION_TARGET,
        DynamoDbTestConstants.TEST_DELETION_KEY_VALUE
      )
    }
    val expectedKey = mapOf(
      DynamoDbTestConstants.TEST_PARTITION_KEY_NAME to AttributeValue.S(
        DynamoDbTestConstants.TEST_CUSTOMER_ID
      ),
      DynamoDbTestConstants.TEST_SORT_KEY_NAME to AttributeValue.S(
        DynamoDbTestConstants.TEST_SORT_KEY_VALUE
      )
    )
    val expectedRequest = DeleteItemRequest {
      tableName = DynamoDbTestConstants.TEST_TABLE_NAME
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
          awsRegion = DynamoDbTestConstants.TEST_AWS_REGION,
          tableName = DynamoDbTestConstants.TEST_TABLE_NAME,
          partitionKeyName = DynamoDbTestConstants.TEST_PARTITION_KEY_NAME,
          deletionKeySchema =  DynamoDbTestConstants.TEST_DELETION_KEY_SCHEMA_NO_SORT
        ),
        DynamoDbTestConstants.TEST_DELETION_KEY_VALUE_NO_SORT
      )
    }
    val expectedKey = mapOf(
      DynamoDbTestConstants.TEST_PARTITION_KEY_NAME to AttributeValue.S(
        DynamoDbTestConstants.TEST_CUSTOMER_ID
      )
    )
    val expectedRequest = DeleteItemRequest {
      tableName = DynamoDbTestConstants.TEST_TABLE_NAME
      key = expectedKey
    }
    runBlocking {
      verify(mockDdbClient).deleteItem(expectedRequest)
    }
  }
}
