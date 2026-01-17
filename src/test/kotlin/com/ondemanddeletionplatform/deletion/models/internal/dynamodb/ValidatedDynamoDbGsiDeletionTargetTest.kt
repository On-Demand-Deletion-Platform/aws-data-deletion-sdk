package com.ondemanddeletionplatform.deletion.models.internal.dynamodb

import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionStrategyType
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionTarget
import com.ondemanddeletionplatform.deletion.testutil.DynamoDbTestConstants
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class ValidatedDynamoDbGsiDeletionTargetTest {
  @Test
  fun canConstructWithoutOptionalFields() {
    val gsiDeletionTarget = ValidatedDynamoDbGsiDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.GSI_QUERY,
      awsRegion = DynamoDbTestConstants.AWS_REGION,
      tableName = DynamoDbTestConstants.TABLE_NAME,
      partitionKeyName = DynamoDbTestConstants.PARTITION_KEY_NAME,
      gsiName = DynamoDbTestConstants.GSI_NAME,
      deletionKeySchema = DynamoDbTestConstants.GSI_DELETION_KEY_SCHEMA_NO_SORT
    )

    assertEquals(DynamoDbDeletionStrategyType.GSI_QUERY, gsiDeletionTarget.strategy)
    assertEquals(DynamoDbTestConstants.AWS_REGION, gsiDeletionTarget.awsRegion)
    assertEquals(DynamoDbTestConstants.TABLE_NAME, gsiDeletionTarget.tableName)
    assertEquals(DynamoDbTestConstants.PARTITION_KEY_NAME, gsiDeletionTarget.partitionKeyName)
    assertEquals(DynamoDbTestConstants.GSI_NAME, gsiDeletionTarget.gsiName)
    assertEquals(DynamoDbTestConstants.GSI_PARTITION_KEY_NAME, gsiDeletionTarget.deletionKeySchema.primaryKeyName)
    assertNull(gsiDeletionTarget.sortKeyName)
    assertNull(gsiDeletionTarget.deletionKeySchema.secondaryKeyName)
  }

  @Test
  fun incorrectStrategy_throwsException() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.TABLE_KEY,
      awsRegion = DynamoDbTestConstants.AWS_REGION,
      tableName = DynamoDbTestConstants.TABLE_NAME,
      partitionKeyName = DynamoDbTestConstants.PARTITION_KEY_NAME,
      deletionKeySchema = DynamoDbTestConstants.GSI_DELETION_KEY_SCHEMA
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
      awsRegion = DynamoDbTestConstants.AWS_REGION,
      tableName = DynamoDbTestConstants.TABLE_NAME,
      partitionKeyName = DynamoDbTestConstants.PARTITION_KEY_NAME,
      deletionKeySchema = DynamoDbTestConstants.DELETION_KEY_SCHEMA_NO_SORT
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
      awsRegion = DynamoDbTestConstants.AWS_REGION,
      tableName = DynamoDbTestConstants.TABLE_NAME,
      partitionKeyName = DynamoDbTestConstants.PARTITION_KEY_NAME,
      gsiName = DynamoDbTestConstants.GSI_NAME,
      deletionKeySchema = DynamoDbTestConstants.GSI_DELETION_KEY_SCHEMA_NO_SORT
    )

    val validatedTarget = ValidatedDynamoDbGsiDeletionTarget.fromDeletionTarget(deletionTarget)

    assertEquals(DynamoDbDeletionStrategyType.GSI_QUERY, validatedTarget.strategy)
    assertEquals(DynamoDbTestConstants.AWS_REGION, validatedTarget.awsRegion)
    assertEquals(DynamoDbTestConstants.TABLE_NAME, validatedTarget.tableName)
    assertEquals(DynamoDbTestConstants.PARTITION_KEY_NAME, validatedTarget.partitionKeyName)
    assertEquals(DynamoDbTestConstants.GSI_NAME, validatedTarget.gsiName)
    assertEquals(DynamoDbTestConstants.GSI_PARTITION_KEY_NAME, validatedTarget.deletionKeySchema.primaryKeyName)
    assertNull(validatedTarget.deletionKeySchema.secondaryKeyName)
  }

  @Test
  fun validInputWithSortKey_returnsValidatedGsiDeletionTarget() {
    val validatedTarget = ValidatedDynamoDbGsiDeletionTarget.fromDeletionTarget(
      DynamoDbTestConstants.GSI_DELETION_TARGET_NO_SORT
    )

    assertEquals(DynamoDbDeletionStrategyType.GSI_QUERY, validatedTarget.strategy)
    assertEquals(DynamoDbTestConstants.AWS_REGION, validatedTarget.awsRegion)
    assertEquals(DynamoDbTestConstants.TABLE_NAME, validatedTarget.tableName)
    assertEquals(DynamoDbTestConstants.PARTITION_KEY_NAME, validatedTarget.partitionKeyName)
    assertEquals(DynamoDbTestConstants.GSI_NAME, validatedTarget.gsiName)
    assertEquals(DynamoDbTestConstants.GSI_PARTITION_KEY_NAME, validatedTarget.deletionKeySchema.primaryKeyName)
    assertEquals(DynamoDbTestConstants.GSI_SORT_KEY_NAME, validatedTarget.deletionKeySchema.secondaryKeyName)
  }
}
