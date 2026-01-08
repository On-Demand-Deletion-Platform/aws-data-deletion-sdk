package com.ondemanddeletionplatform.deletionworker.domain.models

/**
 * Data model representing a DynamoDB deletion key value,
 * which can be used for table key deletions, GSI query-based
 * deletions, or scan-based deletions.
 */
data class DynamoDbDeletionKeyValue(
  // Primary key, used for table partition key, GSI partition key,
  // or primary attribute in table scans.
  val primaryKeyValue: String,

  // Secondary key, used for table sort key, GSI sort key,
  // or secondary attribute filter in table scans.
  val secondaryKeyValue: String? = null
)
