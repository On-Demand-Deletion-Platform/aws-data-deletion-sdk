package com.ondemanddeletionplatform.deletion.models.s3

/**
 * Supported file formats for parsing S3 files to delete
 * a specific customer's data from mixed-customer files.
 */
enum class FileFormat {
  /**
   * JSON Line file format, where each line in the file is a valid JSON object.
   * See: https://jsonlines.org/
   */
  JSONL,

  /**
   * Parquet file format, a column-oriented data format designed for efficient storage and queries.
   * See: https://parquet.apache.org/
   */
  PARQUET
}
