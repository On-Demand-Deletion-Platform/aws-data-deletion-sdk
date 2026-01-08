package com.ondemanddeletionplatform.deletionworker.testutil

import com.ondemanddeletionplatform.deletionworker.domain.models.DynamoDbDeletionKeySchema
import com.ondemanddeletionplatform.deletionworker.domain.models.DynamoDbDeletionKeyValue

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
}
