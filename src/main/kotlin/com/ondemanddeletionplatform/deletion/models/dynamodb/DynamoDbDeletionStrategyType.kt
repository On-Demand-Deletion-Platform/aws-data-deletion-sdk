package com.ondemanddeletionplatform.deletion.models.dynamodb

/**
 * Supported strategies for on-demand data deletion from DynamoDB tables.
 */
enum class DynamoDbDeletionStrategyType {
  /**
   * Delete item by table primary key (partition key and sort key if applicable).
   * Most efficient strategy when you know the exact primary key of items to delete.
   */
  TABLE_KEY,

  /**
   * Query Global Secondary Index for all matching items, then delete each item.
   * Use when you need to find items by GSI attributes rather than table primary key.
   */
  GSI_QUERY,

  /**
   * Scan entire table for all matching items, then delete each item.
   * Least efficient strategy, use only when other strategies are not applicable.
   */
  SCAN
}
