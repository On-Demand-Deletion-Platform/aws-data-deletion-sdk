package com.ondemanddeletionplatform.deletion.models.dynamodb

import com.ondemanddeletionplatform.deletion.testutil.DynamoDbTestConstants
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class DynamoDbDeletionTargetTest {
  @Test
  fun optionalAttrsDefaultToNull() {
    val partitionKeyDeletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.TABLE_KEY,
      awsRegion = DynamoDbTestConstants.AWS_REGION,
      tableName = DynamoDbTestConstants.TABLE_NAME,
      partitionKeyName = DynamoDbTestConstants.PARTITION_KEY_NAME,
      deletionKeySchema = DynamoDbDeletionKeySchema(
        primaryKeyName = DynamoDbTestConstants.PARTITION_KEY_NAME
      )
    )
    assertNotNull(partitionKeyDeletionTarget, "DynamoDbDeletionTarget should be created")
    assertEquals(DynamoDbDeletionStrategyType.TABLE_KEY, partitionKeyDeletionTarget.strategy, "strategy should be PARTITION_KEY")
    assertEquals(DynamoDbTestConstants.AWS_REGION, partitionKeyDeletionTarget.awsRegion, "awsRegion should match")
    assertEquals(DynamoDbTestConstants.TABLE_NAME, partitionKeyDeletionTarget.tableName, "tableName should match")
    assertEquals(
      DynamoDbTestConstants.PARTITION_KEY_NAME,
      partitionKeyDeletionTarget.partitionKeyName,
      "partitionKeyName should match"
    )
    assertEquals(
      DynamoDbTestConstants.PARTITION_KEY_NAME,
      partitionKeyDeletionTarget.deletionKeySchema.primaryKeyName,
      "deletionKeySchema.primaryKeyName should match"
    )
    assertNull(partitionKeyDeletionTarget.sortKeyName, "sortKeyName should default to null")
    assertNull(partitionKeyDeletionTarget.gsiName, "gsiName should default to null")
    assertNull(partitionKeyDeletionTarget.deletionKeySchema.secondaryKeyName, "deletionKeySchema.secondaryKeyName should default to null")
  }
}
