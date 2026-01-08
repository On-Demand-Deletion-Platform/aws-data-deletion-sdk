package com.ondemanddeletionplatform.deletionworker.domain.models

import com.ondemanddeletionplatform.deletionworker.domain.constants.DynamoDbDeletionStrategy

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
