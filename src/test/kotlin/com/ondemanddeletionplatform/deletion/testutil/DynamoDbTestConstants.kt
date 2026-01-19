package com.ondemanddeletionplatform.deletion.testutil

import aws.sdk.kotlin.services.dynamodb.model.ProvisionedThroughput
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionKeySchema
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionKeyValue
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionStrategyType
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionTarget

object DynamoDbTestConstants {
  private const val DDB_CAPACITY_UNITS = 10L

  const val AWS_ACCOUNT_ID = "123456789012"
  const val AWS_REGION = "us-west-2"
  const val TABLE_NAME = "TestTable"
  const val PARTITION_KEY_NAME = "CustomerId"
  const val SORT_KEY_NAME = "SortKey"
  const val GSI_NAME = "GsiIndex"
  const val GSI_PARTITION_KEY_NAME = "GsiPartitionKey"
  const val GSI_SORT_KEY_NAME = "GsiSortKey"
  const val TABLE_DELETION_KEY_NAME = "DeletionKey"
  const val CUSTOMER_ID = "Customer123"
  const val SORT_KEY_VALUE = "SortValue456"
  const val SORT_KEY_VALUE_2 = "sortKeyVal2"
  const val CUSTOMER_ID_1 = "customer1"
  const val CUSTOMER_ID_2 = "customer2"
  const val CUSTOMER_ID_3 = "customer3"
  const val CUSTOMER_ID_4 = "customer4"
  const val CUSTOMER_ID_5 = "customer5"
  const val WAIT_TIME_BETWEEN_DDB_OPERATIONS_MS = 100L

  val PROVISIONED_THROUGHPUT: ProvisionedThroughput = ProvisionedThroughput {
    readCapacityUnits = DDB_CAPACITY_UNITS
    writeCapacityUnits = DDB_CAPACITY_UNITS
  }

  val DELETION_KEY_SCHEMA = DynamoDbDeletionKeySchema(
    primaryKeyName = PARTITION_KEY_NAME,
    secondaryKeyName = SORT_KEY_NAME
  )
  val DELETION_KEY_VALUE = DynamoDbDeletionKeyValue(
    primaryKeyValue = CUSTOMER_ID,
    secondaryKeyValue = SORT_KEY_VALUE
  )
  val DELETION_KEY_SCHEMA_NO_SORT = DynamoDbDeletionKeySchema(
    primaryKeyName = PARTITION_KEY_NAME
  )
  val DELETION_KEY_VALUE_NO_SORT = DynamoDbDeletionKeyValue(
    primaryKeyValue = CUSTOMER_ID
  )
  val GSI_DELETION_KEY_SCHEMA = DynamoDbDeletionKeySchema(
    primaryKeyName = GSI_PARTITION_KEY_NAME,
    secondaryKeyName = GSI_SORT_KEY_NAME
  )
  val GSI_DELETION_KEY_SCHEMA_NO_SORT = DynamoDbDeletionKeySchema(
    primaryKeyName = GSI_PARTITION_KEY_NAME
  )

  val TABLE_KEY_DELETION_TARGET = DynamoDbDeletionTarget(
    strategy = DynamoDbDeletionStrategyType.TABLE_KEY,
    awsAccountId = AWS_ACCOUNT_ID,
    awsRegion = AWS_REGION,
    tableName = TABLE_NAME,
    partitionKeyName = PARTITION_KEY_NAME,
    sortKeyName = SORT_KEY_NAME,
    deletionKeySchema = DELETION_KEY_SCHEMA
  )
  val GSI_DELETION_TARGET = DynamoDbDeletionTarget(
    strategy = DynamoDbDeletionStrategyType.GSI_QUERY,
    awsAccountId = AWS_ACCOUNT_ID,
    awsRegion = AWS_REGION,
    tableName = TABLE_NAME,
    partitionKeyName = PARTITION_KEY_NAME,
    gsiName = GSI_NAME,
    deletionKeySchema = GSI_DELETION_KEY_SCHEMA_NO_SORT
  )
  val GSI_DELETION_TARGET_NO_SORT = DynamoDbDeletionTarget(
    strategy = DynamoDbDeletionStrategyType.GSI_QUERY,
    awsAccountId = AWS_ACCOUNT_ID,
    awsRegion = AWS_REGION,
    tableName = TABLE_NAME,
    partitionKeyName = PARTITION_KEY_NAME,
    sortKeyName = SORT_KEY_NAME,
    gsiName = GSI_NAME,
    deletionKeySchema = GSI_DELETION_KEY_SCHEMA
  )
  val SCAN_DELETION_TARGET = DynamoDbDeletionTarget(
    strategy = DynamoDbDeletionStrategyType.SCAN,
    awsAccountId = AWS_ACCOUNT_ID,
    awsRegion = AWS_REGION,
    tableName = TABLE_NAME,
    partitionKeyName = PARTITION_KEY_NAME,
    sortKeyName = SORT_KEY_NAME,
    deletionKeySchema = DELETION_KEY_SCHEMA
  )
  val SCAN_DELETION_TARGET_NO_SORT = DynamoDbDeletionTarget(
    strategy = DynamoDbDeletionStrategyType.SCAN,
    awsAccountId = AWS_ACCOUNT_ID,
    awsRegion = AWS_REGION,
    tableName = TABLE_NAME,
    partitionKeyName = PARTITION_KEY_NAME,
    deletionKeySchema = DELETION_KEY_SCHEMA_NO_SORT
  )
}
