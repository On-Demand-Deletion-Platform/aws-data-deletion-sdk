package com.ondemanddeletionplatform.deletion.models.dynamodb

/**
 * Data model representing a DynamoDB deletion key value,
 * which can be used for table key deletions, GSI query-based
 * deletions, or scan-based deletions.
 *
 * @property primaryKeyValue Primary key value, used for table partition key,
 *   GSI partition key, or primary attribute in table scans
 * @property secondaryKeyValue Secondary key value, used for table sort key,
 *   GSI sort key, or secondary attribute filter in table scans. Optional.
 */
data class DynamoDbDeletionKeyValue(
  /**
   * Primary key value, used for table partition key, GSI partition key,
   * or primary attribute in table scans.
   */
  val primaryKeyValue: String,

  /**
   * Secondary key value, used for table sort key, GSI sort key,
   * or secondary attribute filter in table scans. Optional.
   */
  val secondaryKeyValue: String? = null
)
