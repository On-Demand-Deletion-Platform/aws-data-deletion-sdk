package com.ondemanddeletionplatform.deletionworker.testutil

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
