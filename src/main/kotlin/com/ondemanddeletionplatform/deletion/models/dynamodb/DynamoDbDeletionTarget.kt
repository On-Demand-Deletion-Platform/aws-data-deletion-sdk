package com.ondemanddeletionplatform.deletion.models.dynamodb

/**
 * Common data model for on-demand deletion strategies for DynamoDB tables.
 */
data class DynamoDbDeletionTarget(
  // The deletion strategy to use for this target.
  val strategy: DynamoDbDeletionStrategyType,

  // Common parameters
  val awsRegion: String,
  val tableName: String,
  val partitionKeyName: String,
  val sortKeyName: String? = null,
  val deletionKeySchema: DynamoDbDeletionKeySchema,

  // GSI deletion strategy parameters
  val gsiName: String? = null
)
