package com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb

import com.ondemanddeletionplatform.deletionworker.testutil.DynamoDbTestConstants
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class DynamoDbDeletionTargetTest {
  @Test
  fun optionalAttrsDefaultToNull() {
    val partitionKeyDeletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.TABLE_KEY,
      awsRegion = DynamoDbTestConstants.TEST_AWS_REGION,
      tableName = DynamoDbTestConstants.TEST_TABLE_NAME,
      partitionKeyName = DynamoDbTestConstants.TEST_PARTITION_KEY_NAME,
      deletionKeySchema = DynamoDbDeletionKeySchema(
        primaryKeyName = DynamoDbTestConstants.TEST_PARTITION_KEY_NAME
      )
    )
    assertNotNull(partitionKeyDeletionTarget, "DynamoDbDeletionTarget should be created")
    assertEquals(DynamoDbDeletionStrategyType.TABLE_KEY, partitionKeyDeletionTarget.strategy, "strategy should be PARTITION_KEY")
    assertEquals(DynamoDbTestConstants.TEST_AWS_REGION, partitionKeyDeletionTarget.awsRegion, "awsRegion should match")
    assertEquals(DynamoDbTestConstants.TEST_TABLE_NAME, partitionKeyDeletionTarget.tableName, "tableName should match")
    assertEquals(
      DynamoDbTestConstants.TEST_PARTITION_KEY_NAME,
      partitionKeyDeletionTarget.partitionKeyName,
      "partitionKeyName should match"
    )
    assertEquals(
      DynamoDbTestConstants.TEST_PARTITION_KEY_NAME,
      partitionKeyDeletionTarget.deletionKeySchema.primaryKeyName,
      "deletionKeySchema.primaryKeyName should match"
    )
    assertNull(partitionKeyDeletionTarget.sortKeyName, "sortKeyName should default to null")
    assertNull(partitionKeyDeletionTarget.gsiName, "gsiName should default to null")
    assertNull(partitionKeyDeletionTarget.deletionKeySchema.secondaryKeyName, "deletionKeySchema.secondaryKeyName should default to null")
  }
}
