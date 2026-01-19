package com.ondemanddeletionplatform.deletion.models.internal.s3

import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionStrategyType
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionTarget
import com.ondemanddeletionplatform.deletion.testutil.S3TestConstants
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.regex.Pattern

class ValidatedS3ObjectKeyDeletionTargetTest {
  @Test
  fun canConstructWithoutOptionalFields() {
    val target = ValidatedS3ObjectKeyDeletionTarget(
      strategy = S3DeletionStrategyType.OBJECT_KEY,
      awsAccountId = S3TestConstants.AWS_ACCOUNT_ID,
      awsRegion = S3TestConstants.AWS_REGION,
      bucketName = S3TestConstants.BUCKET_NAME,
      objectKeyPrefix = null,
      deletionKeyPattern = S3TestConstants.DELETION_KEY_PATTERN
    )

    assertEquals(S3DeletionStrategyType.OBJECT_KEY, target.strategy)
    assertEquals(S3TestConstants.AWS_REGION, target.awsRegion)
    assertEquals(S3TestConstants.BUCKET_NAME, target.bucketName)
    assertNull(target.objectKeyPrefix)
    assertEquals(S3TestConstants.DELETION_KEY_PATTERN, target.deletionKeyPattern)
  }

  @Test
  fun incorrectStrategy_throwsException() {
    val deletionTarget = S3DeletionTarget(
      strategy = S3DeletionStrategyType.ROW_LEVEL,
      awsAccountId = S3TestConstants.AWS_ACCOUNT_ID,
      awsRegion = S3TestConstants.AWS_REGION,
      bucketName = S3TestConstants.BUCKET_NAME,
      objectKeyPrefix = S3TestConstants.OBJECT_KEY_PREFIX,
      deletionKeyPattern = S3TestConstants.DELETION_KEY_PATTERN
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      ValidatedS3ObjectKeyDeletionTarget.fromDeletionTarget(deletionTarget)
    }
    assertEquals("Deletion target strategy must be OBJECT_KEY", exception.message)
  }

  @Test
  fun nullDeletionKeyPattern_throwsException() {
    val deletionTarget = S3DeletionTarget(
      strategy = S3DeletionStrategyType.OBJECT_KEY,
      awsAccountId = S3TestConstants.AWS_ACCOUNT_ID,
      awsRegion = S3TestConstants.AWS_REGION,
      bucketName = S3TestConstants.BUCKET_NAME,
      objectKeyPrefix = S3TestConstants.OBJECT_KEY_PREFIX,
      deletionKeyPattern = null
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      ValidatedS3ObjectKeyDeletionTarget.fromDeletionTarget(deletionTarget)
    }
    assertEquals("Deletion key pattern must not be null", exception.message)
  }

  @Test
  fun deletionKeyPatternWithNoCaptureGroups_throwsException() {
    val patternWithoutCaptureGroup = Pattern.compile("data/customers/\\w+/.*")
    val deletionTarget = S3DeletionTarget(
      strategy = S3DeletionStrategyType.OBJECT_KEY,
      awsAccountId = S3TestConstants.AWS_ACCOUNT_ID,
      awsRegion = S3TestConstants.AWS_REGION,
      bucketName = S3TestConstants.BUCKET_NAME,
      objectKeyPrefix = S3TestConstants.OBJECT_KEY_PREFIX,
      deletionKeyPattern = patternWithoutCaptureGroup
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      ValidatedS3ObjectKeyDeletionTarget.fromDeletionTarget(deletionTarget)
    }
    assertEquals("Deletion key pattern must have exactly one capture group", exception.message)
  }

  @Test
  fun deletionKeyPatternWithMultipleCaptureGroups_throwsException() {
    val patternWithMultipleCaptureGroups = Pattern.compile("data/customers/(\\w+)/(\\w+)/.*")
    val deletionTarget = S3DeletionTarget(
      strategy = S3DeletionStrategyType.OBJECT_KEY,
      awsAccountId = S3TestConstants.AWS_ACCOUNT_ID,
      awsRegion = S3TestConstants.AWS_REGION,
      bucketName = S3TestConstants.BUCKET_NAME,
      objectKeyPrefix = S3TestConstants.OBJECT_KEY_PREFIX,
      deletionKeyPattern = patternWithMultipleCaptureGroups
    )

    val exception = assertThrows(IllegalArgumentException::class.java) {
      ValidatedS3ObjectKeyDeletionTarget.fromDeletionTarget(deletionTarget)
    }
    assertEquals("Deletion key pattern must have exactly one capture group", exception.message)
  }

  @Test
  fun validDeletionTarget_withObjectKeyPrefix_returnsValidatedTarget() {
    val validatedTarget = ValidatedS3ObjectKeyDeletionTarget.fromDeletionTarget(S3TestConstants.OBJECT_KEY_DELETION_TARGET)

    assertEquals(S3DeletionStrategyType.OBJECT_KEY, validatedTarget.strategy)
    assertEquals(S3TestConstants.AWS_REGION, validatedTarget.awsRegion)
    assertEquals(S3TestConstants.BUCKET_NAME, validatedTarget.bucketName)
    assertEquals(S3TestConstants.OBJECT_KEY_PREFIX, validatedTarget.objectKeyPrefix)
    assertEquals(S3TestConstants.DELETION_KEY_PATTERN, validatedTarget.deletionKeyPattern)
  }

  @Test
  fun validDeletionTarget_withoutObjectKeyPrefix_returnsValidatedTarget() {
    val deletionTarget = S3DeletionTarget(
      strategy = S3DeletionStrategyType.OBJECT_KEY,
      awsAccountId = S3TestConstants.AWS_ACCOUNT_ID,
      awsRegion = S3TestConstants.AWS_REGION,
      bucketName = S3TestConstants.BUCKET_NAME,
      objectKeyPrefix = null,
      deletionKeyPattern = S3TestConstants.DELETION_KEY_PATTERN
    )

    val validatedTarget = ValidatedS3ObjectKeyDeletionTarget.fromDeletionTarget(deletionTarget)

    assertEquals(S3DeletionStrategyType.OBJECT_KEY, validatedTarget.strategy)
    assertEquals(S3TestConstants.AWS_REGION, validatedTarget.awsRegion)
    assertEquals(S3TestConstants.BUCKET_NAME, validatedTarget.bucketName)
    assertNull(validatedTarget.objectKeyPrefix)
    assertEquals(S3TestConstants.DELETION_KEY_PATTERN, validatedTarget.deletionKeyPattern)
  }
}
