package com.ondemanddeletionplatform.deletionworker.domain.models

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
    private const val TEST_SORT_KEY_NAME = "SortKey"
    private val TEST_DELETION_KEY_WITHOUT_SORT_KEY = DynamoDbDeletionKeySchema(
      primaryKeyName = TEST_PARTITION_KEY_NAME
    )
    private val TEST_DELETION_KEY_WITH_SORT_KEY = DynamoDbDeletionKeySchema(
      primaryKeyName = TEST_PARTITION_KEY_NAME,
      secondaryKeyName = TEST_SORT_KEY_NAME
    )
  }

  @Test
  fun canConstructWithoutOptionalFields() {
    val tableKeyDeletionTarget = ValidatedDynamoDbTableKeyDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.TABLE_KEY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      deletionKeySchema = TEST_DELETION_KEY_WITHOUT_SORT_KEY
    )

    assertEquals(DynamoDbDeletionStrategyType.TABLE_KEY, tableKeyDeletionTarget.strategy)
    assertEquals(TEST_AWS_REGION, tableKeyDeletionTarget.awsRegion)
    assertEquals(TEST_TABLE_NAME, tableKeyDeletionTarget.tableName)
    assertEquals(TEST_PARTITION_KEY_NAME, tableKeyDeletionTarget.partitionKeyName)
    assertNull(tableKeyDeletionTarget.sortKeyName)
    assertNull(tableKeyDeletionTarget.deletionKeySchema.secondaryKeyName)
  }

  @Test
  fun incorrectStrategy_throwsException() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.GSI_QUERY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      deletionKeySchema = TEST_DELETION_KEY_WITHOUT_SORT_KEY
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      ValidatedDynamoDbTableKeyDeletionTarget.fromDeletionTarget(deletionTarget)
    }
    assertEquals("Deletion target strategy must be TABLE_KEY", exception.message)
  }

  @Test
  fun missingSortKeyValue_throwsException() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.TABLE_KEY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      sortKeyName = TEST_SORT_KEY_NAME,
      deletionKeySchema = TEST_DELETION_KEY_WITHOUT_SORT_KEY
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      ValidatedDynamoDbTableKeyDeletionTarget.fromDeletionTarget(deletionTarget)
    }
    assertEquals("If sortKeyName is provided, deletionKeySchema.secondaryKeyName must also be provided", exception.message)
  }

  @Test
  fun missingSortKeyName_throwsException() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.TABLE_KEY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      deletionKeySchema = TEST_DELETION_KEY_WITH_SORT_KEY
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      ValidatedDynamoDbTableKeyDeletionTarget.fromDeletionTarget(deletionTarget)
    }
    assertEquals("If deletionKeySchema.secondaryKeyName is provided, sortKeyName must also be provided", exception.message)
  }

  @Test
  fun validDeletionTarget_withoutSortKey_returnsValidatedTarget() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.TABLE_KEY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      deletionKeySchema = TEST_DELETION_KEY_WITHOUT_SORT_KEY
    )

    val validatedTarget = ValidatedDynamoDbTableKeyDeletionTarget.fromDeletionTarget(deletionTarget)

    assertEquals(DynamoDbDeletionStrategyType.TABLE_KEY, validatedTarget.strategy)
    assertEquals(TEST_AWS_REGION, validatedTarget.awsRegion)
    assertEquals(TEST_TABLE_NAME, validatedTarget.tableName)
    assertEquals(TEST_PARTITION_KEY_NAME, validatedTarget.partitionKeyName)
    assertEquals(TEST_PARTITION_KEY_NAME, validatedTarget.deletionKeySchema.primaryKeyName)
    assertNull(validatedTarget.sortKeyName)
  }

  @Test
  fun validDeletionTarget_withSortKey_returnsValidatedTarget() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.TABLE_KEY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      sortKeyName = TEST_SORT_KEY_NAME,
      deletionKeySchema = TEST_DELETION_KEY_WITH_SORT_KEY
    )

    val validatedTarget = ValidatedDynamoDbTableKeyDeletionTarget.fromDeletionTarget(deletionTarget)

    assertEquals(DynamoDbDeletionStrategyType.TABLE_KEY, validatedTarget.strategy)
    assertEquals(TEST_AWS_REGION, validatedTarget.awsRegion)
    assertEquals(TEST_TABLE_NAME, validatedTarget.tableName)
    assertEquals(TEST_PARTITION_KEY_NAME, validatedTarget.partitionKeyName)
    assertEquals(TEST_PARTITION_KEY_NAME, validatedTarget.deletionKeySchema.primaryKeyName)
    assertEquals(TEST_SORT_KEY_NAME, validatedTarget.sortKeyName)
    assertEquals(TEST_SORT_KEY_NAME, validatedTarget.deletionKeySchema.secondaryKeyName)
  }
}
