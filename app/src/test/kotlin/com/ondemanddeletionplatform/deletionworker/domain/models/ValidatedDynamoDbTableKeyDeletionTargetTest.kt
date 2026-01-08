package com.ondemanddeletionplatform.deletionworker.domain.models

import com.ondemanddeletionplatform.deletionworker.domain.constants.DynamoDbDeletionStrategy
import org.checkerframework.checker.units.qual.m

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows

class ValidatedDynamoDbTableKeyDeletionTargetTest {
  companion object {
    private const val TEST_AWS_REGION = "us-west-2"
    private const val TEST_TABLE_NAME = "TestTable"
    private const val TEST_PARTITION_KEY_NAME = "CustomerId"
    private const val TEST_PARTITION_KEY_VALUE = "Customer123"
    private const val TEST_SORT_KEY_NAME = "SortKey"
    private const val TEST_SORT_KEY_VALUE = "SortValue456"
  }

  @Test
  fun incorrectStrategy_throwsException() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategy.GSI_QUERY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      ValidatedDynamoDbTableKeyDeletionTarget.fromDeletionTarget(deletionTarget)
    }
    assertEquals("Deletion target strategy must be PARTITION_KEY", exception.message)
  }

  @Test
  fun missingPartitionKeyValue_throwsException() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategy.PARTITION_KEY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      partitionKeyValue = null
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      ValidatedDynamoDbTableKeyDeletionTarget.fromDeletionTarget(deletionTarget)
    }
    assertEquals("Partition key value must not be null", exception.message)
  }

  @Test
  fun missingSortKeyValueWithSortKeyName_throwsException() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategy.PARTITION_KEY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      partitionKeyValue = TEST_PARTITION_KEY_VALUE,
      sortKeyName = TEST_SORT_KEY_NAME,
      sortKeyValue = null
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      ValidatedDynamoDbTableKeyDeletionTarget.fromDeletionTarget(deletionTarget)
    }
    assertEquals("Sort key value must be provided if sort key name is provided", exception.message)
  }

  @Test
  fun missingSortKeyNameWithSortKeyValue_throwsException() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategy.PARTITION_KEY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      partitionKeyValue = TEST_PARTITION_KEY_VALUE,
      sortKeyName = null,
      sortKeyValue = TEST_SORT_KEY_VALUE
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      ValidatedDynamoDbTableKeyDeletionTarget.fromDeletionTarget(deletionTarget)
    }
    assertEquals("Sort key name must be provided if sort key value is provided", exception.message)
  }

  @Test
  fun validDeletionTarget_withoutSortKey_returnsValidatedTarget() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategy.PARTITION_KEY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      partitionKeyValue = TEST_PARTITION_KEY_VALUE,
      sortKeyName = null,
      sortKeyValue = null
    )

    val validatedTarget = ValidatedDynamoDbTableKeyDeletionTarget.fromDeletionTarget(deletionTarget)

    assertEquals(DynamoDbDeletionStrategy.PARTITION_KEY, validatedTarget.strategy)
    assertEquals(TEST_AWS_REGION, validatedTarget.awsRegion)
    assertEquals(TEST_TABLE_NAME, validatedTarget.tableName)
    assertEquals(TEST_PARTITION_KEY_NAME, validatedTarget.partitionKeyName)
    assertEquals(TEST_PARTITION_KEY_VALUE, validatedTarget.partitionKeyValue)
    assertNull(validatedTarget.sortKeyName)
    assertNull(validatedTarget.sortKeyValue)
  }

  @Test
  fun validDeletionTarget_withSortKey_returnsValidatedTarget() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategy.PARTITION_KEY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      partitionKeyValue = TEST_PARTITION_KEY_VALUE,
      sortKeyName = TEST_SORT_KEY_NAME,
      sortKeyValue = TEST_SORT_KEY_VALUE
    )

    val validatedTarget = ValidatedDynamoDbTableKeyDeletionTarget.fromDeletionTarget(deletionTarget)

    assertEquals(DynamoDbDeletionStrategy.PARTITION_KEY, validatedTarget.strategy)
    assertEquals(TEST_AWS_REGION, validatedTarget.awsRegion)
    assertEquals(TEST_TABLE_NAME, validatedTarget.tableName)
    assertEquals(TEST_PARTITION_KEY_NAME, validatedTarget.partitionKeyName)
    assertEquals(TEST_PARTITION_KEY_VALUE, validatedTarget.partitionKeyValue)
    assertEquals(TEST_SORT_KEY_NAME, validatedTarget.sortKeyName)
    assertEquals(TEST_SORT_KEY_VALUE, validatedTarget.sortKeyValue)
  }
}
