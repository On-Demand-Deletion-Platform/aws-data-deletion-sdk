package com.ondemanddeletionplatform.deletionworker.domain.models

import com.ondemanddeletionplatform.deletionworker.domain.constants.DynamoDbDeletionStrategy

data class ValidatedDynamoDbTableKeyDeletionTarget(
  val strategy: DynamoDbDeletionStrategy,
  val awsRegion: String,
  val tableName: String,
  val partitionKeyName: String,
  val partitionKeyValue: String,
  val sortKeyName: String? = null,
  val sortKeyValue: String? = null
) {
  companion object {
    fun fromDeletionTarget(deletionTarget: DynamoDbDeletionTarget): ValidatedDynamoDbTableKeyDeletionTarget {
      require(deletionTarget.strategy == DynamoDbDeletionStrategy.TABLE_KEY) {
        "Deletion target strategy must be PARTITION_KEY"
      }
      requireNotNull(deletionTarget.partitionKeyValue) { "Partition key value must not be null" }
      require(deletionTarget.sortKeyName == null || deletionTarget.sortKeyValue != null) {
        "Sort key value must be provided if sort key name is provided"
      }
      require(deletionTarget.sortKeyName != null || deletionTarget.sortKeyValue == null) {
        "Sort key name must be provided if sort key value is provided"
      }

      return ValidatedDynamoDbTableKeyDeletionTarget(
        strategy = deletionTarget.strategy,
        awsRegion = deletionTarget.awsRegion,
        tableName = deletionTarget.tableName,
        partitionKeyName = deletionTarget.partitionKeyName,
        partitionKeyValue = deletionTarget.partitionKeyValue,
        sortKeyName = deletionTarget.sortKeyName,
        sortKeyValue = deletionTarget.sortKeyValue
      )
    }
  }
}
