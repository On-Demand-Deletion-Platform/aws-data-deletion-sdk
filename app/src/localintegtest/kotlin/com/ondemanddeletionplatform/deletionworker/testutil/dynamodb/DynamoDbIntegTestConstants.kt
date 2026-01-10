package com.ondemanddeletionplatform.deletionworker.testutil.dynamodb

import aws.sdk.kotlin.services.dynamodb.model.AttributeDefinition
import aws.sdk.kotlin.services.dynamodb.model.KeySchemaElement
import aws.sdk.kotlin.services.dynamodb.model.KeyType
import aws.sdk.kotlin.services.dynamodb.model.ProvisionedThroughput
import aws.sdk.kotlin.services.dynamodb.model.ScalarAttributeType

object DynamoDbIntegTestConstants {
  private const val DDB_CAPACITY_UNITS = 10L

  const val AWS_REGION = "us-west-2"
  const val TABLE_NAME = "customers"
  const val PARTITION_KEY_NAME = "id"
  const val SORT_KEY_NAME = "sortKey"
  const val SORT_KEY_VALUE = "sortKeyVal"

  const val CUSTOMER_ID_1 = "customer1"
  const val CUSTOMER_ID_2 = "customer2"

  const val WAIT_TIME_BEFORE_TABLE_CREATION_MS = 3000L
  const val WAIT_TIME_BETWEEN_DDB_OPERATIONS_MS = 500L
  const val MAX_ATTEMPTS_DDB_AVAILABILITY = 10

  val PROVISIONED_THROUGHPUT: ProvisionedThroughput = ProvisionedThroughput {
    readCapacityUnits = DDB_CAPACITY_UNITS
    writeCapacityUnits = DDB_CAPACITY_UNITS
  }

  val ATTR_DEF_PARTITION_KEY: AttributeDefinition = AttributeDefinition {
    attributeName = PARTITION_KEY_NAME
    attributeType = ScalarAttributeType.S
  }
  val ATTR_DEF_SORT_KEY: AttributeDefinition = AttributeDefinition {
    attributeName = SORT_KEY_NAME
    attributeType = ScalarAttributeType.S
  }

  val KEY_SCHEMA_ELEMENT_PARTITION_KEY: KeySchemaElement = KeySchemaElement {
    attributeName = PARTITION_KEY_NAME
    keyType = KeyType.Hash
  }
  val KEY_SCHEMA_ELEMENT_SORT_KEY: KeySchemaElement = KeySchemaElement {
    attributeName = SORT_KEY_NAME
    keyType = KeyType.Range
  }
}
