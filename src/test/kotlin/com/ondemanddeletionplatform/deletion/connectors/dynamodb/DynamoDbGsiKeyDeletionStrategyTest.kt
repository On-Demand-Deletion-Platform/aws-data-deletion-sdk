package com.ondemanddeletionplatform.deletion.connectors.dynamodb

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.DeleteItemRequest
import aws.sdk.kotlin.services.dynamodb.model.QueryResponse
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionKeyValue
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionTarget
import com.ondemanddeletionplatform.deletion.testutil.DynamoDbTestConstants
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

class DynamoDbGsiKeyDeletionStrategyTest {
  @Test
  fun invalidStrategy_throwsException() {
    val mockDdbClient: DynamoDbClient = mock()
    val exception = assertThrows(IllegalArgumentException::class.java) {
      runBlocking {
        DynamoDbGsiKeyDeletionStrategy().deleteData(
          mockDdbClient,
          DynamoDbTestConstants.TABLE_KEY_DELETION_TARGET,
          DynamoDbTestConstants.DELETION_KEY_VALUE
        )
      }
    }
    assertEquals("Deletion target strategy must be GSI_QUERY", exception.message)
    verifyNoInteractions(mockDdbClient)
  }

  @Test
  fun nullQueryResults_succeeds() {
    runBlocking {
      val mockDdbClient: DynamoDbClient = mock()
      val mockDdbQueryResults = QueryResponse {
        items = null
      }
      whenever(mockDdbClient.query(any())).thenReturn(mockDdbQueryResults)

      processRequestAndValidateNoDeletions(
        mockDdbClient,
        DynamoDbTestConstants.GSI_DELETION_TARGET,
        DynamoDbTestConstants.DELETION_KEY_VALUE
      )
    }
  }

  @Test
  fun emptyQueryResults_succeeds() {
    runBlocking {
      val mockDdbClient: DynamoDbClient = mock()
      val mockDdbQueryResults = QueryResponse {
        items = emptyList()
      }
      whenever(mockDdbClient.query(any())).thenReturn(mockDdbQueryResults)

      processRequestAndValidateNoDeletions(
        mockDdbClient,
        DynamoDbTestConstants.GSI_DELETION_TARGET,
        DynamoDbTestConstants.DELETION_KEY_VALUE
      )
    }
  }

  @Test
  fun multiplePagesQueryResults_deletesItems() {
    val customerId1PartitionKey = AttributeValue.S("customer1")
    val customerId2PartitionKey = AttributeValue.S("customer2")
    val customerId3PartitionKey = AttributeValue.S("customer3")
    val matchingCustomerPartitionKeys = listOf(
      customerId1PartitionKey,
      customerId2PartitionKey,
      customerId3PartitionKey
    )
    val customerSortKey = AttributeValue.S(DynamoDbTestConstants.SORT_KEY_VALUE)

    val firstPageResults = QueryResponse {
      items = listOf(
        mapOf(
          DynamoDbTestConstants.PARTITION_KEY_NAME to customerId1PartitionKey,
          DynamoDbTestConstants.SORT_KEY_NAME to customerSortKey
        ),
        mapOf(
          DynamoDbTestConstants.PARTITION_KEY_NAME to customerId2PartitionKey,
          DynamoDbTestConstants.SORT_KEY_NAME to customerSortKey
        )
      )
      lastEvaluatedKey = mapOf(
        DynamoDbTestConstants.PARTITION_KEY_NAME to customerId2PartitionKey,
        DynamoDbTestConstants.SORT_KEY_NAME to customerSortKey
      )
    }
    val secondPageResults = QueryResponse {
      items = listOf(
        mapOf(
          DynamoDbTestConstants.PARTITION_KEY_NAME to customerId3PartitionKey,
          DynamoDbTestConstants.SORT_KEY_NAME to customerSortKey
        )
      )
      lastEvaluatedKey = emptyMap()
    }

    runBlocking {
      val mockDdbClient: DynamoDbClient = mock()
      whenever(mockDdbClient.query(any())).thenReturn(firstPageResults, secondPageResults)

      DynamoDbGsiKeyDeletionStrategy().deleteData(
        mockDdbClient,
        DynamoDbTestConstants.GSI_DELETION_TARGET_NO_SORT,
        DynamoDbTestConstants.DELETION_KEY_VALUE
      )
      verify(mockDdbClient, times(2)).query(any())
      verify(mockDdbClient, times(matchingCustomerPartitionKeys.size)).deleteItem(any())

      matchingCustomerPartitionKeys.forEach { partitionKey ->
        verify(mockDdbClient).deleteItem(
          DeleteItemRequest {
            tableName = DynamoDbTestConstants.TABLE_NAME
            key = mapOf(
              DynamoDbTestConstants.PARTITION_KEY_NAME to partitionKey,
              DynamoDbTestConstants.SORT_KEY_NAME to customerSortKey
            )
          }
        )
      }
    }
  }

  @Test
  fun validTargetWithoutSortKey_deletesItems() {
    val customerId1PartitionKey = AttributeValue.S("customer1")
    val customerId2PartitionKey = AttributeValue.S("customer2")
    val matchingCustomerPartitionKeys = listOf(
      customerId1PartitionKey,
      customerId2PartitionKey
    )

    val queryResults = QueryResponse {
      items = listOf(
        mapOf(
          DynamoDbTestConstants.PARTITION_KEY_NAME to customerId1PartitionKey
        ),
        mapOf(
          DynamoDbTestConstants.PARTITION_KEY_NAME to customerId2PartitionKey
        )
      )
    }

    runBlocking {
      val mockDdbClient: DynamoDbClient = mock()
      whenever(mockDdbClient.query(any())).thenReturn(queryResults)

      DynamoDbGsiKeyDeletionStrategy().deleteData(
        mockDdbClient,
        DynamoDbTestConstants.GSI_DELETION_TARGET,
        DynamoDbTestConstants.DELETION_KEY_VALUE_NO_SORT
      )
      verify(mockDdbClient).query(any())
      verify(mockDdbClient, times(matchingCustomerPartitionKeys.size)).deleteItem(any())

      matchingCustomerPartitionKeys.forEach { partitionKey ->
        verify(mockDdbClient).deleteItem(
          DeleteItemRequest {
            tableName = DynamoDbTestConstants.TABLE_NAME
            key = mapOf(
              DynamoDbTestConstants.PARTITION_KEY_NAME to partitionKey
            )
          }
        )
      }
    }
  }

  @Test
  fun queryResultsMissingPartitionKey_throwsException() {
    val queryResultsMissingPartitionKey = QueryResponse {
      items = listOf(
        mapOf(
          DynamoDbTestConstants.SORT_KEY_NAME to AttributeValue.S("SomeSortKeyValue")
        )
      )
    }

    runBlocking {
      val mockDdbClient: DynamoDbClient = mock()
      whenever(mockDdbClient.query(any())).thenReturn(queryResultsMissingPartitionKey)

      val exception = assertThrows(IllegalStateException::class.java) {
        runBlocking {
          DynamoDbGsiKeyDeletionStrategy().deleteData(
            mockDdbClient,
            DynamoDbTestConstants.GSI_DELETION_TARGET,
            DynamoDbTestConstants.DELETION_KEY_VALUE
          )
        }
      }
      assertEquals("GSI query result partition key missing or not a string: null", exception.message)
      verify(mockDdbClient).query(any())
      verify(mockDdbClient, never()).deleteItem(any())
    }
  }

  @Test
  fun queryResultsMissingSortKey_throwsException() {
    val queryResultsMissingSortKey = QueryResponse {
      items = listOf(
        mapOf(
          DynamoDbTestConstants.PARTITION_KEY_NAME to AttributeValue.S(DynamoDbTestConstants.CUSTOMER_ID)
        )
      )
    }
    runBlocking {
      val mockDdbClient: DynamoDbClient = mock()
      whenever(mockDdbClient.query(any())).thenReturn(queryResultsMissingSortKey)

      val exception = assertThrows(IllegalStateException::class.java) {
        runBlocking {
          DynamoDbGsiKeyDeletionStrategy().deleteData(
            mockDdbClient,
            DynamoDbTestConstants.GSI_DELETION_TARGET_NO_SORT,
            DynamoDbTestConstants.DELETION_KEY_VALUE
          )
        }
      }
      assertEquals(
        "GSI query result sort key missing or not a string: null",
        exception.message
      )
      verify(mockDdbClient).query(any())
      verify(mockDdbClient, never()).deleteItem(any())
    }
  }

  private suspend fun processRequestAndValidateNoDeletions(
    mockDdbClient: DynamoDbClient,
    deletionTarget: DynamoDbDeletionTarget,
    deletionKey: DynamoDbDeletionKeyValue
  ) {
    DynamoDbGsiKeyDeletionStrategy().deleteData(
      mockDdbClient,
      deletionTarget,
      deletionKey
    )
    verify(mockDdbClient).query(any())
    verify(mockDdbClient, never()).deleteItem(any())
  }
}
