package com.ondemanddeletionplatform.deletionworker.domain.models

import com.ondemanddeletionplatform.deletionworker.domain.constants.DynamoDbDeletionStrategy

data class DynamoDbDeletionTarget(
  val strategy: DynamoDbDeletionStrategy,
  val awsRegion: String,
  val tableName: String,
  val partitionKeyName: String,
  val sortKeyName: String? = null,
  val gsiName: String? = null,
  val gsiPartitionKeyName: String? = null,
  val gsiPartitionKeyValue: String? = null,
  val gsiSortKeyName: String? = null,
  val gsiSortKeyValue: String? = null
)
