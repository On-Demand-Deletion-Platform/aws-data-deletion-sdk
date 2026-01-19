package com.ondemanddeletionplatform.deletion.connectors.s3.internal

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.ListObjectsV2Response
import com.ondemanddeletionplatform.deletion.models.internal.s3.ValidatedS3RowLevelDeletionKeyValue
import com.ondemanddeletionplatform.deletion.models.internal.s3.ValidatedS3RowLevelDeletionTarget
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionKeyValue
import com.ondemanddeletionplatform.deletion.models.s3.S3DeletionTarget
import java.util.regex.Pattern

/**
 * S3 deletion strategy for JSON Line files that deletes rows containing matching attribute values.
 */
internal class S3JsonLineRowLevelDeletionStrategy : S3DeletionStrategy() {
  companion object {
    const val USE_VALIDATED_DELETE_METHOD_MESSAGE = "Use S3JsonLineRowLevelDeletionStrategy.deleteData(" +
      "S3Client, ValidatedS3RowLevelDeletionTarget, ValidatedS3RowLevelDeletionKeyValue)"
  }

  override suspend fun deleteData(s3: S3Client, deletionTarget: S3DeletionTarget, deletionKey: S3DeletionKeyValue) {
    throw NotImplementedError(USE_VALIDATED_DELETE_METHOD_MESSAGE)
  }

  @Suppress("UnusedParameter")
  suspend fun deleteData(
    s3: S3Client,
    deletionTarget: ValidatedS3RowLevelDeletionTarget,
    deletionKey: ValidatedS3RowLevelDeletionKeyValue
  ) {
    val deletionKeyPattern = constructEffectiveDeletionKeyPattern(deletionTarget, deletionKey)
    val objectKeyPrefix = deletionKey.objectKeyPrefix ?: deletionTarget.objectKeyPrefix
    println(
      "Deleting rows from S3 JSONL files with objectKeyPrefix: $objectKeyPrefix, deletionKeyPattern: $deletionKeyPattern" +
        ", deletionRowAttributeValue: ${deletionKey.deletionRowAttributeValue}"
    )

    var continuationToken: String? = null
    do {
      val listObjectsResponse = listS3Objects(s3, deletionTarget.bucketName, objectKeyPrefix, continuationToken)

      processObjects(s3, listObjectsResponse, deletionTarget, deletionKey, deletionKeyPattern, objectKeyPrefix)

      continuationToken = listObjectsResponse.nextContinuationToken
    } while (listObjectsResponse.isTruncated == true)
    TODO("JSON Line row-level deletion not yet implemented")
  }

  @Suppress("ForbiddenComment", "LongParameterList")
  private suspend fun processObjects(
    s3: S3Client,
    listObjectsResponse: ListObjectsV2Response,
    deletionTarget: ValidatedS3RowLevelDeletionTarget,
    deletionKey: ValidatedS3RowLevelDeletionKeyValue,
    deletionKeyPattern: Pattern?,
    objectKeyPrefix: String?
  ) {
    val keysForRetrieval = getObjectKeysMatchingDeletionPattern(listObjectsResponse, deletionKeyPattern).filter { obj ->
      obj.key.endsWith(".json") || obj.key.endsWith(".jsonl")
    }
    println("JSON S3 object keys matching objectKeyPrefix: $objectKeyPrefix, deletionKeyPattern: $deletionKeyPattern: $keysForRetrieval")

    keysForRetrieval.forEach {
      val getObjectResponse = getObject(s3, deletionTarget.bucketName, it.key)

      // TODO: Parse each JSON object in the returned file, and remove all JSON objects where the attribute
      // with name equal to deletionTarget.deletionRowAttributeName has value equal to deletionKey.deletionRowAttributeValue.
      // If any changes were made, upload the updated file back to S3 as a new version of the retrieved S3 Object.
      println(
        "TODO: remove JSON records from $getObjectResponse where " +
          "${deletionTarget.deletionRowAttributeName} = ${deletionKey.deletionRowAttributeValue}"
      )
    }
  }

  private fun constructEffectiveDeletionKeyPattern(
    deletionTarget: ValidatedS3RowLevelDeletionTarget,
    deletionKey: ValidatedS3RowLevelDeletionKeyValue
  ): Pattern? {
    val baseDeletionKeyPattern = deletionTarget.deletionKeyPattern ?: return null
    return constructEffectiveDeletionKeyPattern(baseDeletionKeyPattern, deletionKey.deletionKeyPatternCaptureValue)
  }
}
