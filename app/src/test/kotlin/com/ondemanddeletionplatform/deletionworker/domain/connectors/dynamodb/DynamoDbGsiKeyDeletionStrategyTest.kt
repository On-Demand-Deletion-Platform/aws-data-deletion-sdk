package com.ondemanddeletionplatform.deletionworker.domain.connectors.dynamodb

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import com.ondemanddeletionplatform.deletionworker.testutil.DynamoDbTestConstants
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoInteractions

class DynamoDbGsiKeyDeletionStrategyTest {
  @Test
  fun invalidStrategy_throwsException() {
    val mockDdbClient: DynamoDbClient = mock()
    val exception = assertThrows(IllegalArgumentException::class.java) {
      runBlocking {
        DynamoDbGsiKeyDeletionStrategy().deleteData(mockDdbClient, DynamoDbTestConstants.TEST_TABLE_KEY_DELETION_TARGET, DynamoDbTestConstants.TEST_DELETION_KEY_VALUE)
      }
    }
    assertEquals("Deletion target strategy must be GSI_QUERY", exception.message)
    verifyNoInteractions(mockDdbClient)
  }

  @Test
  fun validDeletionTarget_throwsNotImplementedError() {
    val mockDdbClient: DynamoDbClient = mock()
    val exception = assertThrows(NotImplementedError::class.java) {
      runBlocking {
        DynamoDbGsiKeyDeletionStrategy().deleteData(mockDdbClient, DynamoDbTestConstants.TEST_GSI_DELETION_TARGET, DynamoDbTestConstants.TEST_DELETION_KEY_VALUE)
      }
    }
    assertEquals("GSI deletion strategy not yet implemented", exception.message)
    verifyNoInteractions(mockDdbClient)
  }
}
