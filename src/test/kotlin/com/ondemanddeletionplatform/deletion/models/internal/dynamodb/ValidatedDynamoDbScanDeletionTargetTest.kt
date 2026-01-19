package com.ondemanddeletionplatform.deletion.models.internal.dynamodb

import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionKeySchema
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionStrategyType
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionTarget
import com.ondemanddeletionplatform.deletion.testutil.DynamoDbTestConstants
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class ValidatedDynamoDbScanDeletionTargetTest {
  @Test
  fun canConstructValidatedScanDeletionTarget() {
    val scanDeletionTarget = ValidatedDynamoDbScanDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.SCAN,
      awsAccountId = DynamoDbTestConstants.AWS_ACCOUNT_ID,
      awsRegion = DynamoDbTestConstants.AWS_REGION,
      tableName = DynamoDbTestConstants.TABLE_NAME,
      partitionKeyName = DynamoDbTestConstants.PARTITION_KEY_NAME,
      deletionKeySchema = DynamoDbDeletionKeySchema(
        primaryKeyName = DynamoDbTestConstants.TABLE_DELETION_KEY_NAME
      )
    )

    assertEquals(DynamoDbDeletionStrategyType.SCAN, scanDeletionTarget.strategy)
    assertEquals(DynamoDbTestConstants.AWS_REGION, scanDeletionTarget.awsRegion)
    assertEquals(DynamoDbTestConstants.TABLE_NAME, scanDeletionTarget.tableName)
    assertEquals(DynamoDbTestConstants.PARTITION_KEY_NAME, scanDeletionTarget.partitionKeyName)
    assertEquals(DynamoDbTestConstants.TABLE_DELETION_KEY_NAME, scanDeletionTarget.deletionKeySchema.primaryKeyName)
  }

  @Test
  fun incorrectStrategy_throwsException() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.TABLE_KEY,
      awsAccountId = DynamoDbTestConstants.AWS_ACCOUNT_ID,
      awsRegion = DynamoDbTestConstants.AWS_REGION,
      tableName = DynamoDbTestConstants.TABLE_NAME,
      partitionKeyName = DynamoDbTestConstants.PARTITION_KEY_NAME,
      deletionKeySchema = DynamoDbDeletionKeySchema(
        primaryKeyName = DynamoDbTestConstants.PARTITION_KEY_NAME
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
      awsAccountId = DynamoDbTestConstants.AWS_ACCOUNT_ID,
      awsRegion = DynamoDbTestConstants.AWS_REGION,
      tableName = DynamoDbTestConstants.TABLE_NAME,
      partitionKeyName = DynamoDbTestConstants.PARTITION_KEY_NAME,
      deletionKeySchema = DynamoDbDeletionKeySchema(
        primaryKeyName = DynamoDbTestConstants.TABLE_DELETION_KEY_NAME
      )
    )

    val scanDeletionTarget = ValidatedDynamoDbScanDeletionTarget.fromDeletionTarget(deletionTarget)

    assertEquals(DynamoDbDeletionStrategyType.SCAN, scanDeletionTarget.strategy)
    assertEquals(DynamoDbTestConstants.AWS_REGION, scanDeletionTarget.awsRegion)
    assertEquals(DynamoDbTestConstants.TABLE_NAME, scanDeletionTarget.tableName)
    assertEquals(DynamoDbTestConstants.PARTITION_KEY_NAME, scanDeletionTarget.partitionKeyName)
    assertEquals(DynamoDbTestConstants.TABLE_DELETION_KEY_NAME, scanDeletionTarget.deletionKeySchema.primaryKeyName)
  }

  @Test
  fun validInputWithSortKey_returnsValidatedScanDeletionTarget() {
    val deletionTarget = DynamoDbDeletionTarget(
      strategy = DynamoDbDeletionStrategyType.SCAN,
      awsAccountId = DynamoDbTestConstants.AWS_ACCOUNT_ID,
      awsRegion = DynamoDbTestConstants.AWS_REGION,
      tableName = DynamoDbTestConstants.TABLE_NAME,
      partitionKeyName = DynamoDbTestConstants.PARTITION_KEY_NAME,
      sortKeyName = DynamoDbTestConstants.SORT_KEY_NAME,
      deletionKeySchema = DynamoDbDeletionKeySchema(
        primaryKeyName = DynamoDbTestConstants.TABLE_DELETION_KEY_NAME,
        secondaryKeyName = DynamoDbTestConstants.SORT_KEY_NAME
      )
    )

    val scanDeletionTarget = ValidatedDynamoDbScanDeletionTarget.fromDeletionTarget(deletionTarget)

    assertEquals(DynamoDbDeletionStrategyType.SCAN, scanDeletionTarget.strategy)
    assertEquals(DynamoDbTestConstants.AWS_REGION, scanDeletionTarget.awsRegion)
    assertEquals(DynamoDbTestConstants.TABLE_NAME, scanDeletionTarget.tableName)
    assertEquals(DynamoDbTestConstants.PARTITION_KEY_NAME, scanDeletionTarget.partitionKeyName)
    assertEquals(DynamoDbTestConstants.SORT_KEY_NAME, scanDeletionTarget.sortKeyName)
    assertEquals(DynamoDbTestConstants.TABLE_DELETION_KEY_NAME, scanDeletionTarget.deletionKeySchema.primaryKeyName)
    assertEquals(DynamoDbTestConstants.SORT_KEY_NAME, scanDeletionTarget.deletionKeySchema.secondaryKeyName)
  }
}
