package com.ondemanddeletionplatform.deletionworker.testutil

import aws.sdk.kotlin.services.dynamodb.model.ProvisionedThroughput
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.DynamoDbDeletionKeySchema

object DynamoDbIntegTestConstants {
  private const val DDB_CAPACITY_UNITS = 10L

  const val AWS_REGION = "us-west-2"
  const val TABLE_NAME = "customers"
  const val PARTITION_KEY_NAME = "id"
  const val SORT_KEY_NAME = "sortKey"

  val PROVISIONED_THROUGHPUT: ProvisionedThroughput = ProvisionedThroughput {
    readCapacityUnits = DDB_CAPACITY_UNITS
    writeCapacityUnits = DDB_CAPACITY_UNITS
  }

  val DELETION_KEY_SCHEMA_WITH_SORT_KEY: DynamoDbDeletionKeySchema = DynamoDbDeletionKeySchema(
    primaryKeyName = PARTITION_KEY_NAME,
    secondaryKeyName = SORT_KEY_NAME
  )
  val DELETION_KEY_SCHEMA_WITHOUT_SORT_KEY: DynamoDbDeletionKeySchema = DynamoDbDeletionKeySchema(
    primaryKeyName = PARTITION_KEY_NAME
  )
}
