package com.ondemanddeletionplatform.deletionworker.domain.connectors.dynamodb

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.DeleteItemRequest
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

class DynamoDbDeletionConnectorTest {
  @Test
  fun deleteCustomerByTableKey_successfulDeletion() {
    val mockDdbClient: DynamoDbClient = mock()
    val deletionConnector = DynamoDbDeletionConnector(mockDdbClient)

    val expectedDeleteItemRequest = DeleteItemRequest {
      tableName = DynamoDbTestConstants.TEST_TABLE_NAME
      key = mapOf(
        DynamoDbTestConstants.TEST_PARTITION_KEY_NAME to AttributeValue.S(DynamoDbTestConstants.TEST_CUSTOMER_ID),
        DynamoDbTestConstants.TEST_SORT_KEY_NAME to AttributeValue.S(DynamoDbTestConstants.TEST_SORT_KEY_VALUE)
      )
    }

    runBlocking {
      deletionConnector.deleteData(DynamoDbTestConstants.TEST_TABLE_KEY_DELETION_TARGET, DynamoDbTestConstants.TEST_DELETION_KEY_VALUE)

      verify(mockDdbClient).deleteItem(expectedDeleteItemRequest)
    }
  }

  @Test
  fun deleteCustomerByGsiKey_notYetImplemented() {
    val mockDdbClient: DynamoDbClient = mock()
    val deletionConnector = DynamoDbDeletionConnector(mockDdbClient)

    assertThrows(NotImplementedError::class.java) {
      runBlocking {
        deletionConnector.deleteData(DynamoDbTestConstants.TEST_GSI_DELETION_TARGET, DynamoDbTestConstants.TEST_DELETION_KEY_VALUE)
      }
    }
    verifyNoInteractions(mockDdbClient)
  }

  @Test
  fun deleteCustomerByScan_notYetImplemented() {
    val mockDdbClient: DynamoDbClient = mock()
    val deletionConnector = DynamoDbDeletionConnector(mockDdbClient)

    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.SCAN,
      awsRegion = DynamoDbTestConstants.TEST_AWS_REGION,
      tableName = DynamoDbTestConstants.TEST_TABLE_NAME,
      partitionKeyName = DynamoDbTestConstants.TEST_PARTITION_KEY_NAME,
      deletionKeySchema = DynamoDbTestConstants.TEST_DELETION_KEY_SCHEMA
    )

    assertThrows(NotImplementedError::class.java) {
      runBlocking {
        deletionConnector.deleteData(deletionTarget, DynamoDbTestConstants.TEST_DELETION_KEY_VALUE)
      }
    }
    verifyNoInteractions(mockDdbClient)
  }
}
