package com.ondemanddeletionplatform.deletion.connectors.dynamodb

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.DeleteItemRequest
import aws.sdk.kotlin.services.dynamodb.model.QueryResponse
import aws.sdk.kotlin.services.dynamodb.model.ScanResponse
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionStrategyType
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionTarget
import com.ondemanddeletionplatform.deletion.testutil.DynamoDbTestConstants
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class DynamoDbDeletionConnectorTest {
  @Test
  fun deleteCustomerByTableKey_successfulDeletion() {
    val mockDdbClient: DynamoDbClient = mock()
    val deletionConnector = DynamoDbDeletionConnector(mockDdbClient)

    val expectedDeleteItemRequest = DeleteItemRequest {
      tableName = DynamoDbTestConstants.TABLE_NAME
      key = mapOf(
        DynamoDbTestConstants.PARTITION_KEY_NAME to AttributeValue.S(DynamoDbTestConstants.CUSTOMER_ID),
        DynamoDbTestConstants.SORT_KEY_NAME to AttributeValue.S(DynamoDbTestConstants.SORT_KEY_VALUE)
      )
    }

    runBlocking {
      deletionConnector.deleteData(DynamoDbTestConstants.TABLE_KEY_DELETION_TARGET, DynamoDbTestConstants.DELETION_KEY_VALUE)

      verify(mockDdbClient).deleteItem(expectedDeleteItemRequest)
    }
  }

  @Test
  fun deleteCustomerByGsiKey_success() {
    runBlocking {
      val mockDdbClient: DynamoDbClient = mock()
      val mockDdbQueryResults = QueryResponse {
        items = null
      }
      whenever(mockDdbClient.query(any())).thenReturn(mockDdbQueryResults)

      val deletionConnector = DynamoDbDeletionConnector(mockDdbClient)
      deletionConnector.deleteData(DynamoDbTestConstants.GSI_DELETION_TARGET, DynamoDbTestConstants.DELETION_KEY_VALUE)

      verify(mockDdbClient).query(any())
    }
  }

  @Test
  fun deleteCustomerByScan_success() {
    runBlocking {
      val mockDdbClient: DynamoDbClient = mock()
      val mockDdbScanResults = ScanResponse {
        items = null
      }
      whenever(mockDdbClient.scan(any())).thenReturn(mockDdbScanResults)

      val deletionTarget = DynamoDbDeletionTarget(
        strategy = DynamoDbDeletionStrategyType.SCAN,
        awsRegion = DynamoDbTestConstants.AWS_REGION,
        tableName = DynamoDbTestConstants.TABLE_NAME,
        partitionKeyName = DynamoDbTestConstants.PARTITION_KEY_NAME,
        sortKeyName = DynamoDbTestConstants.SORT_KEY_NAME,
        deletionKeySchema = DynamoDbTestConstants.DELETION_KEY_SCHEMA
      )

      val deletionConnector = DynamoDbDeletionConnector(mockDdbClient)
      deletionConnector.deleteData(deletionTarget, DynamoDbTestConstants.DELETION_KEY_VALUE)

      verify(mockDdbClient).scan(any())
    }
  }
}
