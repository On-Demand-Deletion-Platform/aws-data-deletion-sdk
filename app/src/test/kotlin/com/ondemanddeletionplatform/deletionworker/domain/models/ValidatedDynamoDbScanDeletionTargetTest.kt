package com.ondemanddeletionplatform.deletionworker.domain.models

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows

class ValidatedDynamoDbScanDeletionTargetTest {
  companion object {
    private const val TEST_AWS_REGION = "us-west-2"
    private const val TEST_TABLE_NAME = "TestTable"
    private const val TEST_PARTITION_KEY_NAME = "CustomerId"
    private const val TEST_TABLE_DELETION_KEY_NAME = "DeletionKey"
    private const val TEST_SORT_KEY_NAME = "SortKey"
  }

  @Test
  fun canConstructValidatedScanDeletionTarget() {
    val scanDeletionTarget = ValidatedDynamoDbScanDeletionTarget(
      strategy = DynamoDbDeletionStrategy.SCAN,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      tableDeletionKeyName = TEST_TABLE_DELETION_KEY_NAME
    )

    assertEquals(DynamoDbDeletionStrategy.SCAN, scanDeletionTarget.strategy)
    assertEquals(TEST_AWS_REGION, scanDeletionTarget.awsRegion)
    assertEquals(TEST_TABLE_NAME, scanDeletionTarget.tableName)
    assertEquals(TEST_PARTITION_KEY_NAME, scanDeletionTarget.partitionKeyName)
    assertEquals(TEST_TABLE_DELETION_KEY_NAME, scanDeletionTarget.tableDeletionKeyName)
  }

  @Test
  fun incorrectStrategy_throwsException() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategy.TABLE_KEY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      ValidatedDynamoDbScanDeletionTarget.fromDeletionTarget(deletionTarget)
    }
    assertEquals("Deletion target strategy must be SCAN", exception.message)
  }

  @Test
  fun missingTableDeletionKeyName_throwsException() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategy.SCAN,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      ValidatedDynamoDbScanDeletionTarget.fromDeletionTarget(deletionTarget)
    }
    assertEquals("tableDeletionKeyName must be provided for SCAN deletion strategy", exception.message)
  }

  @Test
  fun validInputWithoutSortKey_returnsValidatedScanDeletionTarget() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategy.SCAN,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      tableDeletionKeyName = TEST_TABLE_DELETION_KEY_NAME
    )

    val scanDeletionTarget = ValidatedDynamoDbScanDeletionTarget.fromDeletionTarget(deletionTarget)

    assertEquals(DynamoDbDeletionStrategy.SCAN, scanDeletionTarget.strategy)
    assertEquals(TEST_AWS_REGION, scanDeletionTarget.awsRegion)
    assertEquals(TEST_TABLE_NAME, scanDeletionTarget.tableName)
    assertEquals(TEST_PARTITION_KEY_NAME, scanDeletionTarget.partitionKeyName)
    assertEquals(TEST_TABLE_DELETION_KEY_NAME, scanDeletionTarget.tableDeletionKeyName)
  }

  @Test
  fun validInputWithSortKey_returnsValidatedScanDeletionTarget() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategy.SCAN,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      sortKeyName = TEST_SORT_KEY_NAME,
      tableDeletionKeyName = TEST_TABLE_DELETION_KEY_NAME
    )

    val scanDeletionTarget = ValidatedDynamoDbScanDeletionTarget.fromDeletionTarget(deletionTarget)

    assertEquals(DynamoDbDeletionStrategy.SCAN, scanDeletionTarget.strategy)
    assertEquals(TEST_AWS_REGION, scanDeletionTarget.awsRegion)
    assertEquals(TEST_TABLE_NAME, scanDeletionTarget.tableName)
    assertEquals(TEST_PARTITION_KEY_NAME, scanDeletionTarget.partitionKeyName)
    assertEquals(TEST_SORT_KEY_NAME, scanDeletionTarget.sortKeyName)
    assertEquals(TEST_TABLE_DELETION_KEY_NAME, scanDeletionTarget.tableDeletionKeyName)
  }
}
