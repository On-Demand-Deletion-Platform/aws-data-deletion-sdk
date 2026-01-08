package com.ondemanddeletionplatform.deletionworker.domain.models

/**
 * Validated data model for the DynamoDB GSI-based deletion strategy.
 *
 * Used for data deletion when deletion requests map to a DynamoDB
 * Global Secondary Index (GSI) primary key.
 */
data class ValidatedDynamoDbGsiDeletionTarget(
  val strategy: DynamoDbDeletionStrategyType,
  val awsRegion: String,
  val tableName: String,
  val partitionKeyName: String,
  val sortKeyName: String? = null,
  val gsiName: String,
  val gsiPartitionKeyName: String,
  val gsiPartitionKeyValue: String,
  val gsiSortKeyName: String? = null,
  val gsiSortKeyValue: String? = null
) {
  companion object {
    fun fromDeletionTarget(deletionTarget: DynamoDbDeletionTarget): ValidatedDynamoDbGsiDeletionTarget {
      require(deletionTarget.strategy == DynamoDbDeletionStrategyType.GSI_QUERY) {
        "Deletion target strategy must be GSI_QUERY"
      }

      requireNotNull(deletionTarget.gsiName) { "GSI name must not be null" }
      requireNotNull(deletionTarget.gsiPartitionKeyName) { "GSI partition key name must not be null" }
      requireNotNull(deletionTarget.gsiPartitionKeyValue) { "GSI partition key value must not be null" }

      require(deletionTarget.gsiSortKeyName == null || deletionTarget.gsiSortKeyValue != null) {
        "GSI sort key value must be provided if GSI sort key name is provided"
      }
      require(deletionTarget.gsiSortKeyName != null || deletionTarget.gsiSortKeyValue == null) {
        "GSI sort key name must be provided if GSI sort key value is provided"
      }

      return ValidatedDynamoDbGsiDeletionTarget(
        strategy = deletionTarget.strategy,
        awsRegion = deletionTarget.awsRegion,
        tableName = deletionTarget.tableName,
        partitionKeyName = deletionTarget.partitionKeyName,
        sortKeyName = deletionTarget.sortKeyName,
        gsiName = deletionTarget.gsiName,
        gsiPartitionKeyName = deletionTarget.gsiPartitionKeyName,
        gsiPartitionKeyValue = deletionTarget.gsiPartitionKeyValue,
        gsiSortKeyName = deletionTarget.gsiSortKeyName,
        gsiSortKeyValue = deletionTarget.gsiSortKeyValue
      )
    }
  }
}
