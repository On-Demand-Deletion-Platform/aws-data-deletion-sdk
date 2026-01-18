package com.ondemanddeletionplatform.deletion.localinteg.connectors.s3

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.CreateBucketRequest
import aws.sdk.kotlin.services.s3.model.HeadObjectRequest
import aws.sdk.kotlin.services.s3.model.ListObjectsV2Request
import aws.sdk.kotlin.services.s3.model.NoSuchKey
import aws.sdk.kotlin.services.s3.model.NotFound
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.ByteStream
import com.ondemanddeletionplatform.deletion.localinteg.testutil.s3.S3LocalStack
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll

abstract class S3IntegTest {
  protected lateinit var localstack: S3LocalStack
  protected lateinit var s3: S3Client

  @BeforeAll
  fun setupLocalStack() {
    localstack = S3LocalStack()
    s3 = localstack.s3
  }

  protected suspend fun createBucket(bucketName: String) {
    s3.createBucket(CreateBucketRequest { bucket = bucketName })
    println("Created bucket: $bucketName")
  }

  protected suspend fun putObject(bucketName: String, key: String, content: String) {
    s3.putObject(
      PutObjectRequest {
        bucket = bucketName
        this.key = key
        body = ByteStream.fromString(content)
      }
    )
    println("Put object: $bucketName/$key")
  }

  protected suspend fun validateObjectsExist(bucketName: String, objectKeys: List<String>) {
    objectKeys.forEach {
      assertTrue(
        checkIfObjectExists(bucketName, it),
        "Expected object to exist: $bucketName/$it"
      )
    }
  }

  protected suspend fun validateObjectsDeleted(bucketName: String, objectKeys: List<String>) {
    objectKeys.forEach {
      assertFalse(
        checkIfObjectExists(bucketName, it),
        "Expected object to be deleted: $bucketName/$it"
      )
    }
  }

  @Suppress("ReturnCount", "SwallowedException")
  private suspend fun checkIfObjectExists(bucketName: String, key: String): Boolean {
    try {
      s3.headObject(
        HeadObjectRequest {
          bucket = bucketName
          this.key = key
        }
      )
      return true
    } catch (e: NoSuchKey) {
      return false
    } catch (e: NotFound) {
      return false
    }
  }

  protected suspend fun listObjects(bucketName: String, prefix: String? = null): List<String> {
    val response = s3.listObjectsV2(
      ListObjectsV2Request {
        bucket = bucketName
        this.prefix = prefix
      }
    )
    return response.contents?.map { it.key!! } ?: emptyList()
  }

  protected suspend fun validateObjectCount(bucketName: String, prefix: String?, expectedCount: Int) {
    val objects = listObjects(bucketName, prefix)
    assertEquals(expectedCount, objects.size, "Expected $expectedCount objects with prefix '$prefix', found: $objects")
  }
}
