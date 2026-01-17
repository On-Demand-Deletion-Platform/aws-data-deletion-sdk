package com.ondemanddeletionplatform.deletion.models.s3

/**
 * Supported strategies for on-demand data deletion from S3 buckets.
 */
enum class S3DeletionStrategyType {
  /**
   * Delete all objects with a given S3 object key pattern.
   */
  OBJECT_KEY,

  /**
   * Remove specific rows from all matching S3 files to
   * remove a specific customer's data from files containing
   * data from multiple customers.
   */
  ROW_LEVEL
}
