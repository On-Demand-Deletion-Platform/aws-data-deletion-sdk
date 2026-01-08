package com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb

/**
 * Data model representing a DynamoDB deletion key schema,
 * which can be used for table key deletions, GSI query-based
 * deletions, or scan-based deletions.
 */
data class DynamoDbDeletionKeySchema(
  // Primary key, used for table partition key, GSI partition key,
  // or primary attribute in table scans.
  val primaryKeyName: String,

  // Secondary key, used for table sort key, GSI sort key,
  // or secondary attribute filter in table scans.
  val secondaryKeyName: String? = null
)
