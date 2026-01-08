package com.ondemanddeletionplatform.deletionworker.domain.models

/**
 * Data model representing a DynamoDB deletion key,
 * which can be used for table key deletions,
 * GSI key deletions, or scan-based deletions.
 */
data class DynamoDbDeletionKey(
  // Primary key, used for table partition key, GSI partition key,
  // or primary attribute in table scans.
  val primaryAttributeName: String,

  // Secondary key, used for table sort key, GSI sort key,
  // or secondary attribute filter in table scans.
  val secondaryAttributeName: String? = null
)
