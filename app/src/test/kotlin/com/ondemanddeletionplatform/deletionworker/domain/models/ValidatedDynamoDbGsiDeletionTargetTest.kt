package com.ondemanddeletionplatform.deletionworker.domain.models

import com.ondemanddeletionplatform.deletionworker.domain.constants.DynamoDbDeletionStrategy

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
    private const val TEST_GSI_PARTITION_KEY_VALUE = "Customer123"
    private const val TEST_GSI_SORT_KEY_NAME = "GsiSortKey"
    private const val TEST_GSI_SORT_KEY_VALUE = "SortValue456"
  }

  @Test
  fun incorrectStrategy_throwsException() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategy.PARTITION_KEY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      ValidatedDynamoDbGsiDeletionTarget.fromDeletionTarget(deletionTarget)
    }
    assertEquals("Deletion target strategy must be GSI_QUERY", exception.message)
  }

  @Test
  fun missingGsiFields_throwsException() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategy.GSI_QUERY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      ValidatedDynamoDbGsiDeletionTarget.fromDeletionTarget(deletionTarget)
    }
    assertEquals("GSI name must not be null", exception.message)
  }

  @Test
  fun missingGsiSortKeyName_throwsException() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategy.GSI_QUERY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      gsiName = TEST_GSI_NAME,
      gsiPartitionKeyName = TEST_GSI_PARTITION_KEY_NAME,
      gsiPartitionKeyValue = TEST_GSI_PARTITION_KEY_VALUE,
      gsiSortKeyValue = TEST_GSI_SORT_KEY_VALUE
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      ValidatedDynamoDbGsiDeletionTarget.fromDeletionTarget(deletionTarget)
    }
    assertEquals("GSI sort key name must be provided if GSI sort key value is provided", exception.message)
  }

  @Test
  fun missingGsiSortKeyValue_throwsException() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategy.GSI_QUERY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      gsiName = TEST_GSI_NAME,
      gsiPartitionKeyName = TEST_GSI_PARTITION_KEY_NAME,
      gsiPartitionKeyValue = TEST_GSI_PARTITION_KEY_VALUE,
      gsiSortKeyName = TEST_GSI_SORT_KEY_NAME
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      ValidatedDynamoDbGsiDeletionTarget.fromDeletionTarget(deletionTarget)
    }
    assertEquals("GSI sort key value must be provided if GSI sort key name is provided", exception.message)
  }

  @Test
  fun validInput_returnsValidatedGsiDeletionTarget() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategy.GSI_QUERY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      gsiName = TEST_GSI_NAME,
      gsiPartitionKeyName = TEST_GSI_PARTITION_KEY_NAME,
      gsiPartitionKeyValue = TEST_GSI_PARTITION_KEY_VALUE
    )

    val validatedTarget = ValidatedDynamoDbGsiDeletionTarget.fromDeletionTarget(deletionTarget)

    assertEquals(DynamoDbDeletionStrategy.GSI_QUERY, validatedTarget.strategy)
    assertEquals(TEST_AWS_REGION, validatedTarget.awsRegion)
    assertEquals(TEST_TABLE_NAME, validatedTarget.tableName)
    assertEquals(TEST_PARTITION_KEY_NAME, validatedTarget.partitionKeyName)
    assertEquals(TEST_GSI_NAME, validatedTarget.gsiName)
    assertEquals(TEST_GSI_PARTITION_KEY_NAME, validatedTarget.gsiPartitionKeyName)
    assertEquals(TEST_GSI_PARTITION_KEY_VALUE, validatedTarget.gsiPartitionKeyValue)
    assertNull(validatedTarget.gsiSortKeyName)
    assertNull(validatedTarget.gsiSortKeyValue)
  }

  @Test
  fun validInputWithSortKey_returnsValidatedGsiDeletionTarget() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategy.GSI_QUERY,
      awsRegion = TEST_AWS_REGION,
      tableName = TEST_TABLE_NAME,
      partitionKeyName = TEST_PARTITION_KEY_NAME,
      gsiName = TEST_GSI_NAME,
      gsiPartitionKeyName = TEST_GSI_PARTITION_KEY_NAME,
      gsiPartitionKeyValue = TEST_GSI_PARTITION_KEY_VALUE,
      gsiSortKeyName = TEST_GSI_SORT_KEY_NAME,
      gsiSortKeyValue = TEST_GSI_SORT_KEY_VALUE
    )

    val validatedTarget = ValidatedDynamoDbGsiDeletionTarget.fromDeletionTarget(deletionTarget)

    assertEquals(DynamoDbDeletionStrategy.GSI_QUERY, validatedTarget.strategy)
    assertEquals(TEST_AWS_REGION, validatedTarget.awsRegion)
    assertEquals(TEST_TABLE_NAME, validatedTarget.tableName)
    assertEquals(TEST_PARTITION_KEY_NAME, validatedTarget.partitionKeyName)
    assertEquals(TEST_GSI_NAME, validatedTarget.gsiName)
    assertEquals(TEST_GSI_PARTITION_KEY_NAME, validatedTarget.gsiPartitionKeyName)
    assertEquals(TEST_GSI_PARTITION_KEY_VALUE, validatedTarget.gsiPartitionKeyValue)
    assertEquals(TEST_GSI_SORT_KEY_NAME, validatedTarget.gsiSortKeyName)
    assertEquals(TEST_GSI_SORT_KEY_VALUE, validatedTarget.gsiSortKeyValue)
  }
}
