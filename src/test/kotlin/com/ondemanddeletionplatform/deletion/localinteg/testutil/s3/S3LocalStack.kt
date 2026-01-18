package com.ondemanddeletionplatform.deletion.localinteg.testutil.s3

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.smithy.kotlin.runtime.net.url.Url
import kotlinx.coroutines.runBlocking
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import kotlin.time.Duration.Companion.seconds

class S3LocalStack {
  companion object {
    private const val S3_PORT = 4566
    private const val MAX_ATTEMPTS_S3_AVAILABILITY = 10
    private const val WAIT_TIME_BETWEEN_S3_CHECKS_MS = 3000L
  }

  val localstack: LocalStackContainer
  val s3: S3Client

  constructor() {
    localstack = LocalStackContainer(DockerImageName.parse("localstack/localstack:stable"))
      .withServices(LocalStackContainer.Service.S3)
      .withEnv("SERVICES", "s3") // Required to start S3 service in LocalStack 2.x
      .withEnv("GATEWAY_LISTEN", "$S3_PORT") // Fix the edge port
      .withExposedPorts(S3_PORT) // Expose edge port
      .waitingFor(Wait.forLogMessage(".*Ready\\..*", 1))

    localstack.start()

    val edgePort = localstack.getMappedPort(S3_PORT)
    val s3Endpoint = "http://localhost:$edgePort"
    s3 = S3Client {
      region = localstack.region
      endpointUrl = Url.parse(s3Endpoint)
      credentialsProvider = StaticCredentialsProvider {
        accessKeyId = "test"
        secretAccessKey = "test"
      }
      forcePathStyle = true // Use path-style URLs instead of virtual hosted-style
      httpClient {
        maxConcurrency = 32u
        connectTimeout = 30.seconds
      }
    }
    runBlocking {
      waitForS3()
    }
  }

  fun printLogs() {
    println(localstack.logs)
  }

  /**
   * Waits for the local S3 server to become available by periodically querying ListBuckets.
   *
   * Improves LocalStack integ test reliability since the S3 service can take a few seconds
   * to become available after the container starts.
   */
  private suspend fun waitForS3() {
    println("Waiting for S3 availability...")

    repeat(MAX_ATTEMPTS_S3_AVAILABILITY) {
      try {
        println("Checking S3 availability (attempt ${it + 1}/$MAX_ATTEMPTS_S3_AVAILABILITY)...")
        s3.listBuckets()
        return
      } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
        println("S3 not available yet: ${e.message}. Retrying after ${WAIT_TIME_BETWEEN_S3_CHECKS_MS}ms...")
        Thread.sleep(WAIT_TIME_BETWEEN_S3_CHECKS_MS)
      }
    }
    error("S3 did not become ready in time")
  }
}
