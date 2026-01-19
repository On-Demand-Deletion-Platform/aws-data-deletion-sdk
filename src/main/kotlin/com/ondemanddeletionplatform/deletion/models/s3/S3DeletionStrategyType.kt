package com.ondemanddeletionplatform.deletion.models.s3

/**
 * Supported strategies for on-demand data deletion from S3 buckets.
 */
enum class S3DeletionStrategyType {
  /**
   * Delete all objects with a given S3 object key pattern.
   * Most efficient strategy for deleting entire customer directories or file sets.
   * Use when customer data is segregated by S3 object key structure.
   */
  OBJECT_KEY,

  /**
   * Remove specific rows from all matching S3 files to
   * remove a specific customer's data from files containing
   * data from multiple customers.
   * Use when customer data is mixed within individual files and needs row-level filtering.
   */
  ROW_LEVEL
}
