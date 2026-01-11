package com.ondemanddeletionplatform.deletion.models.dynamodb

/**
 * Supported strategies for on-demand data deletion from DynamoDB tables.
 */
enum class DynamoDbDeletionStrategyType {
  // Delete item by table primary key (partition key and sort key if applicable)
  TABLE_KEY,

  // Query GSI for all matching items, then delete
  GSI_QUERY,

  // Scan table for all matching items, then delete
  SCAN
}
