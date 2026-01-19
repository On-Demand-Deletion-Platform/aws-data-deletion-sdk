package com.ondemanddeletionplatform.deletion.models.dynamodb

/**
 * Data model representing a DynamoDB deletion key schema,
 * which can be used for table key deletions, GSI query-based
 * deletions, or scan-based deletions.
 *
 * @property primaryKeyName Primary key name, used for table partition key,
 *   GSI partition key, or primary attribute in table scans
 * @property secondaryKeyName Secondary key name, used for table sort key,
 *   GSI sort key, or secondary attribute filter in table scans. Optional.
 */
data class DynamoDbDeletionKeySchema(
  /**
   * Primary key name, used for table partition key, GSI partition key,
   * or primary attribute in table scans.
   */
  val primaryKeyName: String,

  /**
   * Secondary key name, used for table sort key, GSI sort key,
   * or secondary attribute filter in table scans. Optional.
   */
  val secondaryKeyName: String? = null
)
