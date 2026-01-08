package com.ondemanddeletionplatform.deletionworker.domain.models

/**
 * Validated data model for the DynamoDB table scan deletion strategy.
 *
 * Used for data deletion when deletion requests do not map to any
 * table or GSI keys, and require a full table scan to identify
 * all records with a given attribute value.
 *
 * Highly inefficient for large tables; use only when not possible
 * to index tables by a reasonable on-demand-deletion key.
 */
data class ValidatedDynamoDbScanDeletionTarget(
  val strategy: DynamoDbDeletionStrategy,
  val awsRegion: String,
  val tableName: String,
  val partitionKeyName: String,
  val sortKeyName: String? = null,
  val tableDeletionKeyName: String
) {
  companion object {
    fun fromDeletionTarget(deletionTarget: DynamoDbDeletionTarget): ValidatedDynamoDbScanDeletionTarget {
      require(deletionTarget.strategy == DynamoDbDeletionStrategy.SCAN) {
        "Deletion target strategy must be SCAN"
      }
      requireNotNull(deletionTarget.tableDeletionKeyName) {
        "tableDeletionKeyName must be provided for SCAN deletion strategy"
      }

      return ValidatedDynamoDbScanDeletionTarget(
        strategy = deletionTarget.strategy,
        awsRegion = deletionTarget.awsRegion,
        tableName = deletionTarget.tableName,
        partitionKeyName = deletionTarget.partitionKeyName,
        sortKeyName = deletionTarget.sortKeyName,
        tableDeletionKeyName = deletionTarget.tableDeletionKeyName
      )
    }
  }
}
