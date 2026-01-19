package com.ondemanddeletionplatform.deletion.models.internal.dynamodb

import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionStrategyType
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionTarget
import com.ondemanddeletionplatform.deletion.testutil.DynamoDbTestConstants
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class ValidatedDynamoDbTableKeyDeletionTargetTest {
  @Test
  fun canConstructWithoutOptionalFields() {
    val tableKeyDeletionTarget = ValidatedDynamoDbTableKeyDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.TABLE_KEY,
      awsAccountId = DynamoDbTestConstants.AWS_ACCOUNT_ID,
      awsRegion = DynamoDbTestConstants.AWS_REGION,
      tableName = DynamoDbTestConstants.TABLE_NAME,
      partitionKeyName = DynamoDbTestConstants.PARTITION_KEY_NAME,
      deletionKeySchema = DynamoDbTestConstants.DELETION_KEY_SCHEMA_NO_SORT
    )

    assertEquals(DynamoDbDeletionStrategyType.TABLE_KEY, tableKeyDeletionTarget.strategy)
    assertEquals(DynamoDbTestConstants.AWS_REGION, tableKeyDeletionTarget.awsRegion)
    assertEquals(DynamoDbTestConstants.TABLE_NAME, tableKeyDeletionTarget.tableName)
    assertEquals(DynamoDbTestConstants.PARTITION_KEY_NAME, tableKeyDeletionTarget.partitionKeyName)
    assertNull(tableKeyDeletionTarget.sortKeyName)
    assertNull(tableKeyDeletionTarget.deletionKeySchema.secondaryKeyName)
  }

  @Test
  fun incorrectStrategy_throwsException() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.GSI_QUERY,
      awsAccountId = DynamoDbTestConstants.AWS_ACCOUNT_ID,
      awsRegion = DynamoDbTestConstants.AWS_REGION,
      tableName = DynamoDbTestConstants.TABLE_NAME,
      partitionKeyName = DynamoDbTestConstants.PARTITION_KEY_NAME,
      deletionKeySchema = DynamoDbTestConstants.DELETION_KEY_SCHEMA_NO_SORT
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
      awsAccountId = DynamoDbTestConstants.AWS_ACCOUNT_ID,
      awsRegion = DynamoDbTestConstants.AWS_REGION,
      tableName = DynamoDbTestConstants.TABLE_NAME,
      partitionKeyName = DynamoDbTestConstants.PARTITION_KEY_NAME,
      sortKeyName = DynamoDbTestConstants.SORT_KEY_NAME,
      deletionKeySchema = DynamoDbTestConstants.DELETION_KEY_SCHEMA_NO_SORT
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
      awsAccountId = DynamoDbTestConstants.AWS_ACCOUNT_ID,
      awsRegion = DynamoDbTestConstants.AWS_REGION,
      tableName = DynamoDbTestConstants.TABLE_NAME,
      partitionKeyName = DynamoDbTestConstants.PARTITION_KEY_NAME,
      deletionKeySchema = DynamoDbTestConstants.DELETION_KEY_SCHEMA
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
      awsAccountId = DynamoDbTestConstants.AWS_ACCOUNT_ID,
      awsRegion = DynamoDbTestConstants.AWS_REGION,
      tableName = DynamoDbTestConstants.TABLE_NAME,
      partitionKeyName = DynamoDbTestConstants.PARTITION_KEY_NAME,
      deletionKeySchema = DynamoDbTestConstants.DELETION_KEY_SCHEMA_NO_SORT
    )

    val validatedTarget = ValidatedDynamoDbTableKeyDeletionTarget.fromDeletionTarget(deletionTarget)

    assertEquals(DynamoDbDeletionStrategyType.TABLE_KEY, validatedTarget.strategy)
    assertEquals(DynamoDbTestConstants.AWS_REGION, validatedTarget.awsRegion)
    assertEquals(DynamoDbTestConstants.TABLE_NAME, validatedTarget.tableName)
    assertEquals(DynamoDbTestConstants.PARTITION_KEY_NAME, validatedTarget.partitionKeyName)
    assertEquals(DynamoDbTestConstants.PARTITION_KEY_NAME, validatedTarget.deletionKeySchema.primaryKeyName)
    assertNull(validatedTarget.sortKeyName)
  }

  @Test
  fun validDeletionTarget_withSortKey_returnsValidatedTarget() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.TABLE_KEY,
      awsAccountId = DynamoDbTestConstants.AWS_ACCOUNT_ID,
      awsRegion = DynamoDbTestConstants.AWS_REGION,
      tableName = DynamoDbTestConstants.TABLE_NAME,
      partitionKeyName = DynamoDbTestConstants.PARTITION_KEY_NAME,
      sortKeyName = DynamoDbTestConstants.SORT_KEY_NAME,
      deletionKeySchema = DynamoDbTestConstants.DELETION_KEY_SCHEMA
    )

    val validatedTarget = ValidatedDynamoDbTableKeyDeletionTarget.fromDeletionTarget(deletionTarget)

    assertEquals(DynamoDbDeletionStrategyType.TABLE_KEY, validatedTarget.strategy)
    assertEquals(DynamoDbTestConstants.AWS_REGION, validatedTarget.awsRegion)
    assertEquals(DynamoDbTestConstants.TABLE_NAME, validatedTarget.tableName)
    assertEquals(DynamoDbTestConstants.PARTITION_KEY_NAME, validatedTarget.partitionKeyName)
    assertEquals(DynamoDbTestConstants.PARTITION_KEY_NAME, validatedTarget.deletionKeySchema.primaryKeyName)
    assertEquals(DynamoDbTestConstants.SORT_KEY_NAME, validatedTarget.sortKeyName)
    assertEquals(DynamoDbTestConstants.SORT_KEY_NAME, validatedTarget.deletionKeySchema.secondaryKeyName)
  }
}
