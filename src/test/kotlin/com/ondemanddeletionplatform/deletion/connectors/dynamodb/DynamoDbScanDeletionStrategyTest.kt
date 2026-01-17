package com.ondemanddeletionplatform.deletion.connectors.dynamodb

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.DeleteItemRequest
import aws.sdk.kotlin.services.dynamodb.model.ScanResponse
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

class DynamoDbScanDeletionStrategyTest {
  @Test
  fun invalidStrategy_throwsException() {
    val mockDdbClient: DynamoDbClient = mock()
    val exception = assertThrows(IllegalArgumentException::class.java) {
      runBlocking {
        DynamoDbScanDeletionStrategy().deleteData(
          mockDdbClient,
          DynamoDbTestConstants.TABLE_KEY_DELETION_TARGET,
          DynamoDbTestConstants.DELETION_KEY_VALUE
        )
      }
    }
    assertEquals("Deletion target strategy must be SCAN", exception.message)
    verifyNoInteractions(mockDdbClient)
  }

  @Test
  fun nullScanResults_succeeds() {
    runBlocking {
      val mockDdbClient: DynamoDbClient = mock()
      val mockDdbScanResults = ScanResponse {
        items = null
      }
      whenever(mockDdbClient.scan(any())).thenReturn(mockDdbScanResults)

      processRequestAndValidateNoDeletions(
        mockDdbClient,
        DynamoDbTestConstants.SCAN_DELETION_TARGET,
        DynamoDbTestConstants.DELETION_KEY_VALUE
      )
    }
  }

  @Test
  fun emptyScanResults_succeeds() {
    runBlocking {
      val mockDdbClient: DynamoDbClient = mock()
      val mockDdbScanResults = ScanResponse {
        items = emptyList()
      }
      whenever(mockDdbClient.scan(any())).thenReturn(mockDdbScanResults)

      processRequestAndValidateNoDeletions(
        mockDdbClient,
        DynamoDbTestConstants.SCAN_DELETION_TARGET,
        DynamoDbTestConstants.DELETION_KEY_VALUE
      )
    }
  }

  @Test
  fun multiplePagesScanResults_deletesItems() {
    val customerId1PartitionKey = AttributeValue.S("customer1")
    val customerId2PartitionKey = AttributeValue.S("customer2")
    val customerId3PartitionKey = AttributeValue.S("customer3")
    val matchingCustomerPartitionKeys = listOf(
      customerId1PartitionKey,
      customerId2PartitionKey,
      customerId3PartitionKey
    )
    val customerSortKey = AttributeValue.S(DynamoDbTestConstants.SORT_KEY_VALUE)

    val firstPageResults = ScanResponse {
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
    val secondPageResults = ScanResponse {
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
      whenever(mockDdbClient.scan(any())).thenReturn(firstPageResults, secondPageResults)

      DynamoDbScanDeletionStrategy().deleteData(
        mockDdbClient,
        DynamoDbTestConstants.SCAN_DELETION_TARGET,
        DynamoDbTestConstants.DELETION_KEY_VALUE
      )
      verify(mockDdbClient, times(2)).scan(any())
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

    val scanResults = ScanResponse {
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
      whenever(mockDdbClient.scan(any())).thenReturn(scanResults)

      DynamoDbScanDeletionStrategy().deleteData(
        mockDdbClient,
        DynamoDbTestConstants.SCAN_DELETION_TARGET_NO_SORT,
        DynamoDbTestConstants.DELETION_KEY_VALUE_NO_SORT
      )
      verify(mockDdbClient).scan(any())
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
  fun scanResultsMissingPartitionKey_throwsException() {
    val scanResultsMissingPartitionKey = ScanResponse {
      items = listOf(
        mapOf(
          DynamoDbTestConstants.SORT_KEY_NAME to AttributeValue.S("SomeSortKeyValue")
        )
      )
    }

    runBlocking {
      val mockDdbClient: DynamoDbClient = mock()
      whenever(mockDdbClient.scan(any())).thenReturn(scanResultsMissingPartitionKey)

      val exception = assertThrows(IllegalStateException::class.java) {
        runBlocking {
          DynamoDbScanDeletionStrategy().deleteData(
            mockDdbClient,
            DynamoDbTestConstants.SCAN_DELETION_TARGET,
            DynamoDbTestConstants.DELETION_KEY_VALUE
          )
        }
      }
      assertEquals("Scan result partition key missing or not a string: null", exception.message)
      verify(mockDdbClient).scan(any())
      verify(mockDdbClient, never()).deleteItem(any())
    }
  }

  @Test
  fun scanResultsMissingSortKey_throwsException() {
    val scanResultsMissingSortKey = ScanResponse {
      items = listOf(
        mapOf(
          DynamoDbTestConstants.PARTITION_KEY_NAME to AttributeValue.S(DynamoDbTestConstants.CUSTOMER_ID)
        )
      )
    }
    runBlocking {
      val mockDdbClient: DynamoDbClient = mock()
      whenever(mockDdbClient.scan(any())).thenReturn(scanResultsMissingSortKey)

      val exception = assertThrows(IllegalStateException::class.java) {
        runBlocking {
          DynamoDbScanDeletionStrategy().deleteData(
            mockDdbClient,
            DynamoDbTestConstants.SCAN_DELETION_TARGET,
            DynamoDbTestConstants.DELETION_KEY_VALUE
          )
        }
      }
      assertEquals(
        "Scan result sort key missing or not a string: null",
        exception.message
      )
      verify(mockDdbClient).scan(any())
      verify(mockDdbClient, never()).deleteItem(any())
    }
  }

  @Test
  fun mismatchBetweenDeletionKeyAndSchema_throwsException() {
    val mockDdbClient: DynamoDbClient = mock()
    val exception = assertThrows(IllegalArgumentException::class.java) {
      runBlocking {
        DynamoDbScanDeletionStrategy().deleteData(
          mockDdbClient,
          DynamoDbTestConstants.SCAN_DELETION_TARGET,
          DynamoDbTestConstants.DELETION_KEY_VALUE_NO_SORT
        )
      }
    }
    assertEquals(
      "Mismatch between deletion key and deletion key schema " +
        "${DynamoDbTestConstants.DELETION_KEY_SCHEMA}, missing secondary key value",
      exception.message
    )
    verifyNoInteractions(mockDdbClient)
  }

  private suspend fun processRequestAndValidateNoDeletions(
    mockDdbClient: DynamoDbClient,
    deletionTarget: DynamoDbDeletionTarget,
    deletionKey: DynamoDbDeletionKeyValue
  ) {
    DynamoDbScanDeletionStrategy().deleteData(
      mockDdbClient,
      deletionTarget,
      deletionKey
    )
    verify(mockDdbClient).scan(any())
    verify(mockDdbClient, never()).deleteItem(any())
  }
}
