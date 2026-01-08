package com.ondemanddeletionplatform.deletionworker.domain.models

/**
 * Validated data model for the DynamoDB table-key-based deletion strategy.
 *
 * Used for efficient data deletion when deletion requests directly
 * map to the DynamoDB table's primary key.
 */
data class ValidatedDynamoDbTableKeyDeletionTarget(
  val strategy: DynamoDbDeletionStrategyType,
  val awsRegion: String,
  val tableName: String,
  val partitionKeyName: String,
  val sortKeyName: String? = null,
  val deletionKey: DynamoDbDeletionKeySchema
) {
  companion object {
    fun fromDeletionTarget(deletionTarget: DynamoDbDeletionTarget): ValidatedDynamoDbTableKeyDeletionTarget {
      require(deletionTarget.strategy == DynamoDbDeletionStrategyType.TABLE_KEY) {
        "Deletion target strategy must be TABLE_KEY"
      }

      return ValidatedDynamoDbTableKeyDeletionTarget(
        strategy = deletionTarget.strategy,
        awsRegion = deletionTarget.awsRegion,
        tableName = deletionTarget.tableName,
        partitionKeyName = deletionTarget.partitionKeyName,
        sortKeyName = deletionTarget.sortKeyName,
        deletionKey = deletionTarget.deletionKey
      )
    }
  }
}
