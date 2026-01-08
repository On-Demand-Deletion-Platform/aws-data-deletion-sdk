package com.ondemanddeletionplatform.deletionworker.domain.models

import com.ondemanddeletionplatform.deletionworker.testutil.DynamoDbTestConstants
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows

class ValidatedDynamoDbScanDeletionTargetTest {
  @Test
  fun canConstructValidatedScanDeletionTarget() {
    val scanDeletionTarget = ValidatedDynamoDbScanDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.SCAN,
      awsRegion = DynamoDbTestConstants.TEST_AWS_REGION,
      tableName = DynamoDbTestConstants.TEST_TABLE_NAME,
      partitionKeyName = DynamoDbTestConstants.TEST_PARTITION_KEY_NAME,
      deletionKeySchema = DynamoDbDeletionKeySchema(
        primaryKeyName = DynamoDbTestConstants.TEST_TABLE_DELETION_KEY_NAME
      )
    )

    assertEquals(DynamoDbDeletionStrategyType.SCAN, scanDeletionTarget.strategy)
    assertEquals(DynamoDbTestConstants.TEST_AWS_REGION, scanDeletionTarget.awsRegion)
    assertEquals(DynamoDbTestConstants.TEST_TABLE_NAME, scanDeletionTarget.tableName)
    assertEquals(DynamoDbTestConstants.TEST_PARTITION_KEY_NAME, scanDeletionTarget.partitionKeyName)
    assertEquals(DynamoDbTestConstants.TEST_TABLE_DELETION_KEY_NAME, scanDeletionTarget.deletionKeySchema.primaryKeyName)
  }

  @Test
  fun incorrectStrategy_throwsException() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.TABLE_KEY,
      awsRegion = DynamoDbTestConstants.TEST_AWS_REGION,
      tableName = DynamoDbTestConstants.TEST_TABLE_NAME,
      partitionKeyName = DynamoDbTestConstants.TEST_PARTITION_KEY_NAME,
      deletionKeySchema = DynamoDbDeletionKeySchema(
        primaryKeyName = DynamoDbTestConstants.TEST_PARTITION_KEY_NAME
      )
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      ValidatedDynamoDbScanDeletionTarget.fromDeletionTarget(deletionTarget)
    }
    assertEquals("Deletion target strategy must be SCAN", exception.message)
  }

  @Test
  fun validInputWithoutSortKey_returnsValidatedScanDeletionTarget() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.SCAN,
      awsRegion = DynamoDbTestConstants.TEST_AWS_REGION,
      tableName = DynamoDbTestConstants.TEST_TABLE_NAME,
      partitionKeyName = DynamoDbTestConstants.TEST_PARTITION_KEY_NAME,
      deletionKeySchema = DynamoDbDeletionKeySchema(
        primaryKeyName = DynamoDbTestConstants.TEST_TABLE_DELETION_KEY_NAME
      )
    )

    val scanDeletionTarget = ValidatedDynamoDbScanDeletionTarget.fromDeletionTarget(deletionTarget)

    assertEquals(DynamoDbDeletionStrategyType.SCAN, scanDeletionTarget.strategy)
    assertEquals(DynamoDbTestConstants.TEST_AWS_REGION, scanDeletionTarget.awsRegion)
    assertEquals(DynamoDbTestConstants.TEST_TABLE_NAME, scanDeletionTarget.tableName)
    assertEquals(DynamoDbTestConstants.TEST_PARTITION_KEY_NAME, scanDeletionTarget.partitionKeyName)
    assertEquals(DynamoDbTestConstants.TEST_TABLE_DELETION_KEY_NAME, scanDeletionTarget.deletionKeySchema.primaryKeyName)
  }

  @Test
  fun validInputWithSortKey_returnsValidatedScanDeletionTarget() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.SCAN,
      awsRegion = DynamoDbTestConstants.TEST_AWS_REGION,
      tableName = DynamoDbTestConstants.TEST_TABLE_NAME,
      partitionKeyName = DynamoDbTestConstants.TEST_PARTITION_KEY_NAME,
      sortKeyName = DynamoDbTestConstants.TEST_SORT_KEY_NAME,
      deletionKeySchema = DynamoDbDeletionKeySchema(
        primaryKeyName = DynamoDbTestConstants.TEST_TABLE_DELETION_KEY_NAME,
        secondaryKeyName = DynamoDbTestConstants.TEST_SORT_KEY_NAME
      )
    )

    val scanDeletionTarget = ValidatedDynamoDbScanDeletionTarget.fromDeletionTarget(deletionTarget)

    assertEquals(DynamoDbDeletionStrategyType.SCAN, scanDeletionTarget.strategy)
    assertEquals(DynamoDbTestConstants.TEST_AWS_REGION, scanDeletionTarget.awsRegion)
    assertEquals(DynamoDbTestConstants.TEST_TABLE_NAME, scanDeletionTarget.tableName)
    assertEquals(DynamoDbTestConstants.TEST_PARTITION_KEY_NAME, scanDeletionTarget.partitionKeyName)
    assertEquals(DynamoDbTestConstants.TEST_SORT_KEY_NAME, scanDeletionTarget.sortKeyName)
    assertEquals(DynamoDbTestConstants.TEST_TABLE_DELETION_KEY_NAME, scanDeletionTarget.deletionKeySchema.primaryKeyName)
    assertEquals(DynamoDbTestConstants.TEST_SORT_KEY_NAME, scanDeletionTarget.deletionKeySchema.secondaryKeyName)
  }
}
