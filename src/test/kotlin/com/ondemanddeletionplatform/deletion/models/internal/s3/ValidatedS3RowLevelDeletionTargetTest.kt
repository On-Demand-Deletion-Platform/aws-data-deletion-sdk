package com.ondemanddeletionplatform.deletion.models.internal.s3

import com.ondemanddeletionplatform.deletion.models.s3.FileFormat
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionStrategyType
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionTarget
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.regex.Pattern

class ValidatedS3RowLevelDeletionTargetTest {
  @Test
  fun fromDeletionTarget_validRowLevelTarget_success() {
    val deletionTarget = S3DeletionTarget(
      strategy = S3DeletionStrategyType.ROW_LEVEL,
      awsRegion = "us-east-1",
      bucketName = "test-bucket",
      objectKeyPrefix = "data/customers/",
      deletionKeyPattern = Pattern.compile("customer/(\\w+)/.*"),
      deletionRowAttributeName = "customerId",
      objectFileFormat = FileFormat.JSONL
    )

    val validated = ValidatedS3RowLevelDeletionTarget.fromDeletionTarget(deletionTarget)

    assertEquals(S3DeletionStrategyType.ROW_LEVEL, validated.strategy)
    assertEquals("us-east-1", validated.awsRegion)
    assertEquals("test-bucket", validated.bucketName)
    assertEquals("data/customers/", validated.objectKeyPrefix)
    assertEquals("customerId", validated.deletionRowAttributeName)
    assertEquals(FileFormat.JSONL, validated.objectFileFormat)
  }

  @Test
  fun fromDeletionTarget_wrongStrategy_throwsException() {
    val deletionTarget = S3DeletionTarget(
      strategy = S3DeletionStrategyType.OBJECT_KEY,
      awsRegion = "us-east-1",
      bucketName = "test-bucket",
      deletionRowAttributeName = "customerId",
      objectFileFormat = FileFormat.JSONL
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      ValidatedS3RowLevelDeletionTarget.fromDeletionTarget(deletionTarget)
    }
    assertEquals("Deletion target strategy must be ROW_LEVEL", exception.message)
  }

  @Test
  fun fromDeletionTarget_nullDeletionRowAttributeName_throwsException() {
    val deletionTarget = S3DeletionTarget(
      strategy = S3DeletionStrategyType.ROW_LEVEL,
      awsRegion = "us-east-1",
      bucketName = "test-bucket",
      deletionRowAttributeName = null,
      objectFileFormat = FileFormat.JSONL
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      ValidatedS3RowLevelDeletionTarget.fromDeletionTarget(deletionTarget)
    }
    assertEquals("Deletion row attribute name must not be null", exception.message)
  }

  @Test
  fun fromDeletionTarget_nullObjectFileFormat_throwsException() {
    val deletionTarget = S3DeletionTarget(
      strategy = S3DeletionStrategyType.ROW_LEVEL,
      awsRegion = "us-east-1",
      bucketName = "test-bucket",
      deletionRowAttributeName = "customerId",
      objectFileFormat = null
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      ValidatedS3RowLevelDeletionTarget.fromDeletionTarget(deletionTarget)
    }
    assertEquals("Object file format must not be null", exception.message)
  }

  @Test
  fun fromDeletionTarget_patternWithZeroCaptureGroups_throwsException() {
    val deletionTarget = S3DeletionTarget(
      strategy = S3DeletionStrategyType.ROW_LEVEL,
      awsRegion = "us-east-1",
      bucketName = "test-bucket",
      deletionKeyPattern = Pattern.compile("customer/\\w+/.*"),
      deletionRowAttributeName = "customerId",
      objectFileFormat = FileFormat.JSONL
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      ValidatedS3RowLevelDeletionTarget.fromDeletionTarget(deletionTarget)
    }
    assertEquals("Deletion key pattern must have exactly one capture group", exception.message)
  }

  @Test
  fun fromDeletionTarget_patternWithMultipleCaptureGroups_throwsException() {
    val deletionTarget = S3DeletionTarget(
      strategy = S3DeletionStrategyType.ROW_LEVEL,
      awsRegion = "us-east-1",
      bucketName = "test-bucket",
      deletionKeyPattern = Pattern.compile("customer/(\\w+)/(\\w+)/.*"),
      deletionRowAttributeName = "customerId",
      objectFileFormat = FileFormat.JSONL
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      ValidatedS3RowLevelDeletionTarget.fromDeletionTarget(deletionTarget)
    }
    assertEquals("Deletion key pattern must have exactly one capture group", exception.message)
  }

  @Test
  fun fromDeletionTarget_nullPattern_success() {
    val deletionTarget = S3DeletionTarget(
      strategy = S3DeletionStrategyType.ROW_LEVEL,
      awsRegion = "us-east-1",
      bucketName = "test-bucket",
      deletionKeyPattern = null,
      deletionRowAttributeName = "customerId",
      objectFileFormat = FileFormat.PARQUET
    )

    val validated = ValidatedS3RowLevelDeletionTarget.fromDeletionTarget(deletionTarget)

    assertNull(validated.deletionKeyPattern)
    assertEquals(FileFormat.PARQUET, validated.objectFileFormat)
  }
}
