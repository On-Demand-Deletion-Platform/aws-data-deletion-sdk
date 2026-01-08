package com.ondemanddeletionplatform.deletionworker.domain.models

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull

class DynamoDbDeletionTargetTest {
  companion object {
    private const val TEST_AWS_REGION = "us-west-2"
    private const val TEST_TABLE_NAME = "TestTable"
    private const val TEST_PARTITION_KEY_NAME = "CustomerId"
  }

  @Test
  fun optionalAttrsDefaultToNull() {
    val partitionKeyDeletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.TABLE_KEY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      deletionKey = DynamoDbDeletionKey(
        primaryAttributeName = TEST_PARTITION_KEY_NAME
      )
    )
    assertNotNull(partitionKeyDeletionTarget, "DynamoDbDeletionTarget should be created")
    assertEquals(DynamoDbDeletionStrategyType.TABLE_KEY, partitionKeyDeletionTarget.strategy, "strategy should be PARTITION_KEY")
    assertEquals(TEST_AWS_REGION, partitionKeyDeletionTarget.awsRegion, "awsRegion should match")
    assertEquals(TEST_TABLE_NAME, partitionKeyDeletionTarget.tableName, "tableName should match")
    assertEquals(TEST_PARTITION_KEY_NAME, partitionKeyDeletionTarget.partitionKeyName, "partitionKeyName should match")
    assertEquals(TEST_PARTITION_KEY_NAME, partitionKeyDeletionTarget.deletionKey.primaryAttributeName, "deletionKey.primaryAttributeName should match")
    assertNull(partitionKeyDeletionTarget.sortKeyName, "sortKeyName should default to null")
    assertNull(partitionKeyDeletionTarget.gsiName, "gsiName should default to null")
    assertNull(partitionKeyDeletionTarget.deletionKey.secondaryAttributeName, "deletionKey.secondaryAttributeName should default to null")
  }
}
