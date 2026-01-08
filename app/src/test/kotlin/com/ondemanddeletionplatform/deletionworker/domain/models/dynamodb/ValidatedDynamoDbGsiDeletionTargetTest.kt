package com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb

import com.ondemanddeletionplatform.deletionworker.testutil.DynamoDbTestConstants
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows

class ValidatedDynamoDbGsiDeletionTargetTest {
  @Test
  fun canConstructWithoutOptionalFields() {
    val gsiDeletionTarget = ValidatedDynamoDbGsiDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.GSI_QUERY,
      awsRegion = DynamoDbTestConstants.TEST_AWS_REGION,
      tableName = DynamoDbTestConstants.TEST_TABLE_NAME,
      partitionKeyName = DynamoDbTestConstants.TEST_PARTITION_KEY_NAME,
      gsiName = DynamoDbTestConstants.TEST_GSI_NAME,
      deletionKeySchema = DynamoDbTestConstants.TEST_GSI_DELETION_KEY_SCHEMA_NO_SORT
    )

    assertEquals(DynamoDbDeletionStrategyType.GSI_QUERY, gsiDeletionTarget.strategy)
    assertEquals(DynamoDbTestConstants.TEST_AWS_REGION, gsiDeletionTarget.awsRegion)
    assertEquals(DynamoDbTestConstants.TEST_TABLE_NAME, gsiDeletionTarget.tableName)
    assertEquals(DynamoDbTestConstants.TEST_PARTITION_KEY_NAME, gsiDeletionTarget.partitionKeyName)
    assertEquals(DynamoDbTestConstants.TEST_GSI_NAME, gsiDeletionTarget.gsiName)
    assertEquals(DynamoDbTestConstants.TEST_GSI_PARTITION_KEY_NAME, gsiDeletionTarget.deletionKeySchema.primaryKeyName)
    assertNull(gsiDeletionTarget.sortKeyName)
    assertNull(gsiDeletionTarget.deletionKeySchema.secondaryKeyName)
  }

  @Test
  fun incorrectStrategy_throwsException() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.TABLE_KEY,
      awsRegion = DynamoDbTestConstants.TEST_AWS_REGION,
      tableName = DynamoDbTestConstants.TEST_TABLE_NAME,
      partitionKeyName = DynamoDbTestConstants.TEST_PARTITION_KEY_NAME,
      deletionKeySchema = DynamoDbTestConstants.TEST_GSI_DELETION_KEY_SCHEMA
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
      awsRegion = DynamoDbTestConstants.TEST_AWS_REGION,
      tableName = DynamoDbTestConstants.TEST_TABLE_NAME,
      partitionKeyName = DynamoDbTestConstants.TEST_PARTITION_KEY_NAME,
      deletionKeySchema = DynamoDbTestConstants.TEST_DELETION_KEY_SCHEMA_NO_SORT
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
      awsRegion = DynamoDbTestConstants.TEST_AWS_REGION,
      tableName = DynamoDbTestConstants.TEST_TABLE_NAME,
      partitionKeyName = DynamoDbTestConstants.TEST_PARTITION_KEY_NAME,
      gsiName = DynamoDbTestConstants.TEST_GSI_NAME,
      deletionKeySchema = DynamoDbTestConstants.TEST_GSI_DELETION_KEY_SCHEMA_NO_SORT
    )

    val validatedTarget = ValidatedDynamoDbGsiDeletionTarget.fromDeletionTarget(deletionTarget)

    assertEquals(DynamoDbDeletionStrategyType.GSI_QUERY, validatedTarget.strategy)
    assertEquals(DynamoDbTestConstants.TEST_AWS_REGION, validatedTarget.awsRegion)
    assertEquals(DynamoDbTestConstants.TEST_TABLE_NAME, validatedTarget.tableName)
    assertEquals(DynamoDbTestConstants.TEST_PARTITION_KEY_NAME, validatedTarget.partitionKeyName)
    assertEquals(DynamoDbTestConstants.TEST_GSI_NAME, validatedTarget.gsiName)
    assertEquals(DynamoDbTestConstants.TEST_GSI_PARTITION_KEY_NAME, validatedTarget.deletionKeySchema.primaryKeyName)
    assertNull(validatedTarget.deletionKeySchema.secondaryKeyName)
  }

  @Test
  fun validInputWithSortKey_returnsValidatedGsiDeletionTarget() {
    val validatedTarget = ValidatedDynamoDbGsiDeletionTarget.fromDeletionTarget(DynamoDbTestConstants.TEST_GSI_DELETION_TARGET)

    assertEquals(DynamoDbDeletionStrategyType.GSI_QUERY, validatedTarget.strategy)
    assertEquals(DynamoDbTestConstants.TEST_AWS_REGION, validatedTarget.awsRegion)
    assertEquals(DynamoDbTestConstants.TEST_TABLE_NAME, validatedTarget.tableName)
    assertEquals(DynamoDbTestConstants.TEST_PARTITION_KEY_NAME, validatedTarget.partitionKeyName)
    assertEquals(DynamoDbTestConstants.TEST_GSI_NAME, validatedTarget.gsiName)
    assertEquals(DynamoDbTestConstants.TEST_GSI_PARTITION_KEY_NAME, validatedTarget.deletionKeySchema.primaryKeyName)
    assertEquals(DynamoDbTestConstants.TEST_GSI_SORT_KEY_NAME, validatedTarget.deletionKeySchema.secondaryKeyName)
  }
}
