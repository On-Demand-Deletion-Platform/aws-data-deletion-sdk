package com.ondemanddeletionplatform.deletionworker.domain.constants

enum class DynamoDbDeletionStrategy {
  // Delete item by partition key (and sort key if applicable)
  PARTITION_KEY,

  // Query GSI for all matching items, then delete
  GSI_QUERY,

  // Scan table for all matching items, then delete
  SCAN
}
