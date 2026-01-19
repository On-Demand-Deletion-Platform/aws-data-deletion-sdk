package com.ondemanddeletionplatform.deletion.models.s3

import java.util.regex.Pattern

/**
 * Common data model for on-demand deletion strategies for S3 buckets.
 *
 * Instances describe how to delete an onboarded S3 bucket's data
 * in response to all subsequent customer-level deletion requests.
 *
 * @property strategy Deletion strategy to use for this target
 * @property awsRegion AWS region the S3 bucket is located in
 * @property bucketName S3 bucket name
 * @property objectKeyPrefix S3 object key prefix to filter searches for efficiency
 * @property deletionKeyPattern S3 object key pattern with exactly one capture group for the deletion key
 * @property deletionRowAttributeName Attribute name to filter rows by in ROW_LEVEL strategy
 * @property objectFileFormat File format for parsing content in ROW_LEVEL strategy
 */
data class S3DeletionTarget(
  /**
   * Deletion strategy to use for this target.
   */
  val strategy: S3DeletionStrategyType,

  /**
   * AWS region the S3 bucket is located in.
   */
  val awsRegion: String,

  /**
   * S3 bucket name.
   */
  val bucketName: String,

  /**
   * S3 object key prefix to filter to, eg. "data/customers/".
   * Increases efficiency of object searches in nested directories
   * when a deletion strategy is for a specific subdirectory.
   */
  val objectKeyPrefix: String? = null,

  /**
   * S3 object key pattern to search for, with exactly one capture group
   * which contains the deletion primary key value (eg. customer ID).
   * Eg. Pattern.compile("data/purchases/year/\\d{4}/month/\\d{2}/day/\\d{2}/customer/(\\w+)/.*")
   */
  val deletionKeyPattern: Pattern? = null,

  /**
   * If deleting specific rows from S3 files, the in-file attribute
   * to use to filter for a given deletion subject, eg. "customerId".
   * Required for ROW_LEVEL strategy.
   */
  val deletionRowAttributeName: String? = null,

  /**
   * File format to use for parsing content, if need to parse and
   * remove a specific customer's data from mixed-customer files.
   * Required if using S3DeletionStrategyType.ROW_LEVEL.
   */
  val objectFileFormat: FileFormat? = null
)
