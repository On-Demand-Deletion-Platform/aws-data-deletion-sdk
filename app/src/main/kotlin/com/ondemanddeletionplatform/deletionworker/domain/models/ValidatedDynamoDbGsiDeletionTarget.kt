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
  val deletionKeySchema: DynamoDbDeletionKeySchema
) {
  companion object {
    fun fromDeletionTarget(deletionTarget: DynamoDbDeletionTarget): ValidatedDynamoDbGsiDeletionTarget {
      require(deletionTarget.strategy == DynamoDbDeletionStrategyType.GSI_QUERY) {
        "Deletion target strategy must be GSI_QUERY"
      }
      requireNotNull(deletionTarget.gsiName) { "GSI name must not be null" }

      return ValidatedDynamoDbGsiDeletionTarget(
        strategy = deletionTarget.strategy,
        awsRegion = deletionTarget.awsRegion,
        tableName = deletionTarget.tableName,
        partitionKeyName = deletionTarget.partitionKeyName,
        sortKeyName = deletionTarget.sortKeyName,
        gsiName = deletionTarget.gsiName,
        deletionKeySchema = deletionTarget.deletionKeySchema
      )
    }
  }
}
