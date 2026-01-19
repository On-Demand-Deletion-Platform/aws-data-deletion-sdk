package com.ondemanddeletionplatform.deletion.models.s3

/**
 * Common data model for a specific customer-level deletion
 * key, which should be automatically mapped to an onboarded
 * S3 bucket's deletion strategy.
 *
 * @property objectKeyPrefix S3 object key prefix to filter to for efficient searches
 * @property deletionKeyPatternCaptureValue The deletion key value to match against the target's pattern
 * @property deletionRowAttributeValue Value to match when filtering rows in ROW_LEVEL strategy
 */
data class S3DeletionKeyValue(
  /**
   * S3 object key prefix to filter to, eg. "customers/fred/".
   * Increases efficiency of object searches in nested directories
   * when a deletion strategy is for a specific subdirectory.
   */
  val objectKeyPrefix: String? = null,

  /**
   * The deletion key to match to the deletion target's deletionKeyPattern.
   *
   * Eg. Given a deletion target with strategy = S3DeletionStrategyType.OBJECT_KEY
   * and deletionKeyPattern = Pattern.compile("data/purchases/customer/(\\w+)/.*"),
   * and a deletion key with deletionKeyPatternCaptureValue = "fred", we will
   * delete all S3 objects in the given bucket with key prefix "data/purchases/customer/fred/".
   */
  val deletionKeyPatternCaptureValue: String?,

  /**
   * If scanning S3 files for rows matching a given deletion subject,
   * the value of the deletionRowAttributeKey to match on when
   * selecting rows to delete from files.
   */
  val deletionRowAttributeValue: String? = null,
)
