package com.ondemanddeletionplatform.deletionworker.testutil.dynamodb

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeDefinition
import aws.sdk.kotlin.services.dynamodb.model.CreateTableRequest
import aws.sdk.kotlin.services.dynamodb.model.KeySchemaElement
import aws.sdk.kotlin.services.dynamodb.model.KeyType
import aws.sdk.kotlin.services.dynamodb.model.ProvisionedThroughput
import aws.sdk.kotlin.services.dynamodb.model.ScalarAttributeType

class DynamoDbRepositoryUtils {
  companion object {
    private const val DDB_CAPACITY_UNITS = 10L
  }

  suspend fun createTable(dynamoDb: DynamoDbClient, tableName: String, withSortKey: Boolean) {
    val createTableRequest = buildCreateTableRequest(tableName, withSortKey)
    dynamoDb.createTable(createTableRequest)
    println("Created test table $tableName")
  }

  private fun buildCreateTableRequest(tableName: String, withSortKey: Boolean): CreateTableRequest {
    var keySchema = mutableListOf(
      KeySchemaElement {
        attributeName = DynamoDbIntegTestConstants.PARTITION_KEY_NAME
        keyType = KeyType.Hash
      }
    )
    var attributeDefinitions = mutableListOf(
      AttributeDefinition {
        attributeName = DynamoDbIntegTestConstants.PARTITION_KEY_NAME
        attributeType = ScalarAttributeType.S
      }
    )

    if (withSortKey) {
      keySchema.add(
        KeySchemaElement {
          attributeName = DynamoDbIntegTestConstants.SORT_KEY_NAME
          keyType = KeyType.Range
        }
      )
      attributeDefinitions.add(
        AttributeDefinition {
          attributeName = DynamoDbIntegTestConstants.SORT_KEY_NAME
          attributeType = ScalarAttributeType.S
        }
      )
    }

    return CreateTableRequest {
      this.tableName = tableName
      this.keySchema = keySchema
      this.attributeDefinitions = attributeDefinitions
      provisionedThroughput = ProvisionedThroughput {
        readCapacityUnits = DDB_CAPACITY_UNITS
        writeCapacityUnits = DDB_CAPACITY_UNITS
      }
    }
  }
}
