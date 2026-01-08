package com.ondemanddeletionplatform.deletionworker.domain.models

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

  // Table key deletion strategy parameters
  val partitionKeyValue: String? = null,
  val sortKeyValue: String? = null,

  // GSI deletion strategy parameters
  val gsiName: String? = null,
  val gsiPartitionKeyName: String? = null,
  val gsiPartitionKeyValue: String? = null,
  val gsiSortKeyName: String? = null,
  val gsiSortKeyValue: String? = null,

  // Scan deletion strategy parameters
  val tableDeletionKeyName: String? = null
)
