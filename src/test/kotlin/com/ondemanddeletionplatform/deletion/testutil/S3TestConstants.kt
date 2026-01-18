package com.ondemanddeletionplatform.deletion.testutil

import com.ondemanddeletionplatform.deletion.models.s3.FileFormat
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionStrategyType
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionTarget
import java.util.regex.Pattern

object S3TestConstants {
  const val AWS_REGION = "us-west-2"
  const val BUCKET_NAME = "test-bucket"
  const val OBJECT_KEY_PREFIX = "data/customers/"
  const val DELETION_KEY_PATTERN_STRING = "data/customers/(\\w+)/.*"
  const val DELETION_ROW_ATTRIBUTE_NAME = "customerId"

  val DELETION_KEY_PATTERN: Pattern = Pattern.compile(DELETION_KEY_PATTERN_STRING)

  val OBJECT_KEY_DELETION_TARGET = S3DeletionTarget(
    strategy = S3DeletionStrategyType.OBJECT_KEY,
    awsRegion = AWS_REGION,
    bucketName = BUCKET_NAME,
    objectKeyPrefix = OBJECT_KEY_PREFIX,
    deletionKeyPattern = DELETION_KEY_PATTERN
  )

  val ROW_LEVEL_DELETION_TARGET = S3DeletionTarget(
    strategy = S3DeletionStrategyType.ROW_LEVEL,
    awsRegion = AWS_REGION,
    bucketName = BUCKET_NAME,
    objectKeyPrefix = OBJECT_KEY_PREFIX,
    deletionKeyPattern = DELETION_KEY_PATTERN,
    deletionRowAttributeName = DELETION_ROW_ATTRIBUTE_NAME,
    objectFileFormat = FileFormat.JSONL
  )
}
