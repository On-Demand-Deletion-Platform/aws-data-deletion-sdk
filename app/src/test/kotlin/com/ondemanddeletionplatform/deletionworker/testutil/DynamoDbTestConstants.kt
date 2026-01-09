package com.ondemanddeletionplatform.deletionworker.testutil

import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.DynamoDbDeletionKeySchema
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.DynamoDbDeletionKeyValue
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.DynamoDbDeletionStrategyType
import com.ondemanddeletionplatform.deletionworker.domain.models.dynamodb.DynamoDbDeletionTarget

object DynamoDbTestConstants {
  const val TEST_AWS_REGION = "us-west-2"
  const val TEST_TABLE_NAME = "TestTable"
  const val TEST_PARTITION_KEY_NAME = "CustomerId"
  const val TEST_SORT_KEY_NAME = "SortKey"
  const val TEST_GSI_NAME = "GsiIndex"
  const val TEST_GSI_PARTITION_KEY_NAME = "GsiPartitionKey"
  const val TEST_GSI_SORT_KEY_NAME = "GsiSortKey"
  const val TEST_TABLE_DELETION_KEY_NAME = "DeletionKey"
  const val TEST_CUSTOMER_ID = "Customer123"
  const val TEST_SORT_KEY_VALUE = "SortValue456"

  val TEST_DELETION_KEY_SCHEMA = DynamoDbDeletionKeySchema(
    primaryKeyName = TEST_PARTITION_KEY_NAME,
    secondaryKeyName = TEST_SORT_KEY_NAME
  )
  val TEST_DELETION_KEY_VALUE = DynamoDbDeletionKeyValue(
    primaryKeyValue = TEST_CUSTOMER_ID,
    secondaryKeyValue = TEST_SORT_KEY_VALUE
  )
  val TEST_DELETION_KEY_SCHEMA_NO_SORT = DynamoDbDeletionKeySchema(
    primaryKeyName = TEST_PARTITION_KEY_NAME
  )
  val TEST_DELETION_KEY_VALUE_NO_SORT = DynamoDbDeletionKeyValue(
    primaryKeyValue = TEST_CUSTOMER_ID
  )
  val TEST_GSI_DELETION_KEY_SCHEMA = DynamoDbDeletionKeySchema(
    primaryKeyName = TEST_GSI_PARTITION_KEY_NAME,
    secondaryKeyName = TEST_GSI_SORT_KEY_NAME
  )
  val TEST_GSI_DELETION_KEY_SCHEMA_NO_SORT = DynamoDbDeletionKeySchema(
    primaryKeyName = TEST_GSI_PARTITION_KEY_NAME
  )

  val TEST_TABLE_KEY_DELETION_TARGET = DynamoDbDeletionTarget(
    strategy = DynamoDbDeletionStrategyType.TABLE_KEY,
    awsRegion = TEST_AWS_REGION,
    tableName = TEST_TABLE_NAME,
    partitionKeyName = TEST_PARTITION_KEY_NAME,
    sortKeyName = TEST_SORT_KEY_NAME,
    deletionKeySchema = TEST_DELETION_KEY_SCHEMA
  )
  val TEST_GSI_DELETION_TARGET = DynamoDbDeletionTarget(
    strategy = DynamoDbDeletionStrategyType.GSI_QUERY,
    awsRegion = TEST_AWS_REGION,
    tableName = TEST_TABLE_NAME,
    partitionKeyName = TEST_PARTITION_KEY_NAME,
    gsiName = TEST_GSI_NAME,
    deletionKeySchema = TEST_GSI_DELETION_KEY_SCHEMA_NO_SORT
  )
  val TEST_GSI_DELETION_TARGET_NO_SORT = DynamoDbDeletionTarget(
    strategy = DynamoDbDeletionStrategyType.GSI_QUERY,
    awsRegion = TEST_AWS_REGION,
    tableName = TEST_TABLE_NAME,
    partitionKeyName = TEST_PARTITION_KEY_NAME,
    sortKeyName = TEST_SORT_KEY_NAME,
    gsiName = TEST_GSI_NAME,
    deletionKeySchema = TEST_GSI_DELETION_KEY_SCHEMA
  )
}
