package com.ondemanddeletionplatform.deletionworker.domain.models

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows

class ValidatedDynamoDbGsiDeletionTargetTest {
  companion object {
    private const val TEST_AWS_REGION = "us-west-2"
    private const val TEST_TABLE_NAME = "TestTable"
    private const val TEST_PARTITION_KEY_NAME = "CustomerId"
    private const val TEST_GSI_NAME = "GsiIndex"
    private const val TEST_GSI_PARTITION_KEY_NAME = "GsiPartitionKey"
    private const val TEST_GSI_SORT_KEY_NAME = "GsiSortKey"

    private val TEST_DELETION_KEY_WITHOUT_SORT_KEY = DynamoDbDeletionKey(
      primaryAttributeName = TEST_GSI_PARTITION_KEY_NAME
    )
  }

  @Test
  fun canConstructWithoutOptionalFields() {
    val gsiDeletionTarget = ValidatedDynamoDbGsiDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.GSI_QUERY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      gsiName = TEST_GSI_NAME,
      deletionKey = TEST_DELETION_KEY_WITHOUT_SORT_KEY
    )

    assertEquals(DynamoDbDeletionStrategyType.GSI_QUERY, gsiDeletionTarget.strategy)
    assertEquals(TEST_AWS_REGION, gsiDeletionTarget.awsRegion)
    assertEquals(TEST_TABLE_NAME, gsiDeletionTarget.tableName)
    assertEquals(TEST_PARTITION_KEY_NAME, gsiDeletionTarget.partitionKeyName)
    assertEquals(TEST_GSI_NAME, gsiDeletionTarget.gsiName)
    assertEquals(TEST_GSI_PARTITION_KEY_NAME, gsiDeletionTarget.deletionKey.primaryAttributeName)
    assertNull(gsiDeletionTarget.sortKeyName)
    assertNull(gsiDeletionTarget.deletionKey.secondaryAttributeName)
  }

  @Test
  fun incorrectStrategy_throwsException() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.TABLE_KEY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      deletionKey = TEST_DELETION_KEY_WITHOUT_SORT_KEY
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      ValidatedDynamoDbGsiDeletionTarget.fromDeletionTarget(deletionTarget)
    }
    assertEquals("Deletion target strategy must be GSI_QUERY", exception.message)
  }

  @Test
  fun missingGsiFields_throwsException() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.GSI_QUERY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      deletionKey = TEST_DELETION_KEY_WITHOUT_SORT_KEY
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      ValidatedDynamoDbGsiDeletionTarget.fromDeletionTarget(deletionTarget)
    }
    assertEquals("GSI name must not be null", exception.message)
  }

  @Test
  fun validInputWithoutSortKey_returnsValidatedGsiDeletionTarget() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.GSI_QUERY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      gsiName = TEST_GSI_NAME,
      deletionKey = TEST_DELETION_KEY_WITHOUT_SORT_KEY
    )

    val validatedTarget = ValidatedDynamoDbGsiDeletionTarget.fromDeletionTarget(deletionTarget)

    assertEquals(DynamoDbDeletionStrategyType.GSI_QUERY, validatedTarget.strategy)
    assertEquals(TEST_AWS_REGION, validatedTarget.awsRegion)
    assertEquals(TEST_TABLE_NAME, validatedTarget.tableName)
    assertEquals(TEST_PARTITION_KEY_NAME, validatedTarget.partitionKeyName)
    assertEquals(TEST_GSI_NAME, validatedTarget.gsiName)
    assertEquals(TEST_GSI_PARTITION_KEY_NAME, validatedTarget.deletionKey.primaryAttributeName)
    assertNull(validatedTarget.deletionKey.secondaryAttributeName)
  }

  @Test
  fun validInputWithSortKey_returnsValidatedGsiDeletionTarget() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.GSI_QUERY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      gsiName = TEST_GSI_NAME,
      deletionKey =  DynamoDbDeletionKey(
        primaryAttributeName = TEST_GSI_PARTITION_KEY_NAME,
        secondaryAttributeName = TEST_GSI_SORT_KEY_NAME
      )
    )

    val validatedTarget = ValidatedDynamoDbGsiDeletionTarget.fromDeletionTarget(deletionTarget)

    assertEquals(DynamoDbDeletionStrategyType.GSI_QUERY, validatedTarget.strategy)
    assertEquals(TEST_AWS_REGION, validatedTarget.awsRegion)
    assertEquals(TEST_TABLE_NAME, validatedTarget.tableName)
    assertEquals(TEST_PARTITION_KEY_NAME, validatedTarget.partitionKeyName)
    assertEquals(TEST_GSI_NAME, validatedTarget.gsiName)
    assertEquals(TEST_GSI_PARTITION_KEY_NAME, validatedTarget.deletionKey.primaryAttributeName)
    assertEquals(TEST_GSI_SORT_KEY_NAME, validatedTarget.deletionKey.secondaryAttributeName)
  }
}
