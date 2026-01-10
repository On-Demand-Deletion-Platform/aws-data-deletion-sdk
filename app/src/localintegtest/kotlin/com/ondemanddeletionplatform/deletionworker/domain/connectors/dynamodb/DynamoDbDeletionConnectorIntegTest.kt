package com.ondemanddeletionplatform.deletionworker.domain.connectors.dynamodb

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeDefinition
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.CreateTableRequest
import aws.sdk.kotlin.services.dynamodb.model.GetItemRequest
import aws.sdk.kotlin.services.dynamodb.model.GetItemResponse
import aws.sdk.kotlin.services.dynamodb.model.KeySchemaElement
import aws.sdk.kotlin.services.dynamodb.model.KeyType
import aws.sdk.kotlin.services.dynamodb.model.PutItemRequest
import aws.sdk.kotlin.services.dynamodb.model.ScalarAttributeType
import aws.smithy.kotlin.runtime.net.url.Url
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.DynamoDbDeletionKeySchema
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.DynamoDbDeletionKeyValue
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.DynamoDbDeletionStrategyType
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.DynamoDbDeletionTarget
import com.ondemanddeletionplatform.deletionworker.testutil.DynamoDbIntegTestConstants
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import kotlin.time.Duration.Companion.seconds

/**
 * Local DynamoDB deletion connector integ tests using LocalStack.
 *
 * Notes:
 * - These tests require Docker to be installed and running on the host machine.
 * - Can use `println(localstack.logs)` to print container logs when debugging.
 * - If need to debug LocalStack startup issues, update the LocalStackContainer
 *   setup to include the following lines:
 *      .withEnv("LS_LOG", "trace") // Enable detailed logging for debugging
 *      .withEnv("DEBUG", "1") // Enable debug mode
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DynamoDbDeletionConnectorIntegTest {
  companion object {
    private const val DDB_PORT = 4566
    private const val WAIT_TIME_BEFORE_TABLE_CREATION_MS = 3000L
    private const val WAIT_TIME_BETWEEN_DDB_OPERATIONS_MS = 500L
    private const val MAX_ATTEMPTS_DDB_AVAILABILITY = 10

    private const val CUSTOMER_ID_1 = "customer1"
    private const val CUSTOMER_ID_2 = "customer2"
  }

  private lateinit var localstack: LocalStackContainer
  private lateinit var dynamoDb: DynamoDbClient

  @BeforeAll
  fun setupLocalStack() {
    localstack = LocalStackContainer(DockerImageName.parse("localstack/localstack:stable"))
      .withServices(LocalStackContainer.Service.DYNAMODB)
      .withEnv("DYNAMODB_PROVIDER", "dynamodb-local") // Use DynamoDB Local
      .withEnv("SERVICES", "dynamodb") // Required to start DynamoDB service in LocalStack 2.x
      .withEnv("GATEWAY_LISTEN", "$DDB_PORT") // Fix the edge port
      .withExposedPorts(DDB_PORT) // Expose edge port
      .waitingFor(Wait.forLogMessage(".*Ready\\..*", 1))

    localstack.start()

    val edgePort = localstack.getMappedPort(DDB_PORT)
    val dynamoDbEndpoint = "http://localhost:$edgePort"
    dynamoDb = DynamoDbClient {
      region = localstack.region
      endpointUrl = Url.parse(dynamoDbEndpoint)
      credentialsProvider = StaticCredentialsProvider {
        accessKeyId = "test"
        secretAccessKey = "test"
      }
      httpClient {
        maxConcurrency = 32u
        connectTimeout = 30.seconds
      }
    }
    runBlocking {
      waitForDynamoDb()
    }
  }

  @Tag("localIntegTest")
  @Test
  fun testDynamoDbSetup() {
    val tableName = "TestSetupDdbTable"
    runBlocking {
      createCustomerTableWithTestData(tableName)

      Thread.sleep(WAIT_TIME_BETWEEN_DDB_OPERATIONS_MS)

      val getItemResult = getCustomerItem(tableName, CUSTOMER_ID_1)
      assertNotNull(getItemResult.item)
      assertEquals(getItemResult.item?.get(DynamoDbIntegTestConstants.PARTITION_KEY_NAME), AttributeValue.S(CUSTOMER_ID_1))
    }
  }

  @Tag("localIntegTest")
  @Test
  fun tableKeyDeletionStrategyCanDeleteByPrimaryKey() {
    val tableName = "TestDeleteByPrimaryKey"
    runBlocking {
      createCustomerTableWithTestData(tableName)

      val deletionTarget = generateDeletionKeyValue(tableName, null)
      val deletionKeyValue = DynamoDbDeletionKeyValue(
        primaryKeyValue = CUSTOMER_ID_1
      )
      DynamoDbDeletionConnector(dynamoDb).deleteData(deletionTarget, deletionKeyValue)
      val getItemResult1 = getCustomerItem(tableName, CUSTOMER_ID_1)
      assertNull(getItemResult1.item)
      val getItemResult2 = getCustomerItem(tableName, CUSTOMER_ID_2)
      assertNotNull(getItemResult2.item)
      assertEquals(getItemResult2.item?.get(DynamoDbIntegTestConstants.PARTITION_KEY_NAME), AttributeValue.S(CUSTOMER_ID_2))
    }
  }

  /**
   * Waits for the local DynamoDB server to become available by periodically querying ListTables.
   *
   * Improves LocalStack integ test reliability since the DynamoDB service can take a few seconds
   * to become available after the container starts.
   */
  suspend fun waitForDynamoDb() {
    println("Waiting for DynamoDB availability...")

    repeat(MAX_ATTEMPTS_DDB_AVAILABILITY) {
      try {
        println("Checking DynamoDB availability (attempt ${it + 1}/$MAX_ATTEMPTS_DDB_AVAILABILITY)...")
        dynamoDb.listTables()
        return
      } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
        println("DynamoDB not available yet: ${e.message}. Retrying after ${WAIT_TIME_BEFORE_TABLE_CREATION_MS}ms...")
        Thread.sleep(WAIT_TIME_BEFORE_TABLE_CREATION_MS)
      }
    }
    error("DynamoDB did not become ready in time")
  }

  suspend fun createCustomerTableWithTestData(tableName: String) {
    val createTableRequest = CreateTableRequest {
      this.tableName = tableName
      keySchema = listOf(
        KeySchemaElement {
          attributeName = DynamoDbIntegTestConstants.PARTITION_KEY_NAME
          keyType = KeyType.Hash
        }
      )
      attributeDefinitions = listOf(
        AttributeDefinition {
          attributeName = DynamoDbIntegTestConstants.PARTITION_KEY_NAME
          attributeType = ScalarAttributeType.S
        }
      )
      provisionedThroughput = DynamoDbIntegTestConstants.PROVISIONED_THROUGHPUT
    }
    dynamoDb.createTable(createTableRequest)
    println("Created test table")

    Thread.sleep(WAIT_TIME_BETWEEN_DDB_OPERATIONS_MS)

    // Populate test data
    putCustomerItem(tableName, CUSTOMER_ID_1)
    Thread.sleep(WAIT_TIME_BETWEEN_DDB_OPERATIONS_MS)
    putCustomerItem(tableName, CUSTOMER_ID_2)
    println("Populated test data")
  }

  suspend fun putCustomerItem(tableName: String, customerId: String) {
    val customerItem = mapOf(DynamoDbIntegTestConstants.PARTITION_KEY_NAME to AttributeValue.S(customerId))
    dynamoDb.putItem(
      PutItemRequest {
        this.tableName = tableName
        item = customerItem
      }
    )
  }

  suspend fun getCustomerItem(tableName: String, customerId: String): GetItemResponse {
    return dynamoDb.getItem(
      GetItemRequest {
        this.tableName = tableName
        key = mapOf(DynamoDbIntegTestConstants.PARTITION_KEY_NAME to AttributeValue.S(customerId))
      }
    )
  }

  fun generateDeletionKeyValue(tableName: String, sortKey: String?): DynamoDbDeletionTarget {
    val deletionKeySchema = generateDeletionKeySchema(sortKey)

    return DynamoDbDeletionTarget(
      tableName = tableName,
      awsRegion = DynamoDbIntegTestConstants.AWS_REGION,
      strategy = DynamoDbDeletionStrategyType.TABLE_KEY,
      partitionKeyName = DynamoDbIntegTestConstants.PARTITION_KEY_NAME,
      deletionKeySchema = deletionKeySchema
    )
  }

  fun generateDeletionKeySchema(sortKey: String?): DynamoDbDeletionKeySchema {
    if (sortKey == null) {
      return DynamoDbIntegTestConstants.DELETION_KEY_SCHEMA_WITHOUT_SORT_KEY
    }
    return DynamoDbIntegTestConstants.DELETION_KEY_SCHEMA_WITH_SORT_KEY
  }
}
