package com.ondemanddeletionplatform.deletionworker.testutil.dynamodb

object DynamoDbIntegTestConstants {
  const val AWS_REGION = "us-west-2"
  const val TABLE_NAME = "customers"
  const val PARTITION_KEY_NAME = "id"
  const val SORT_KEY_NAME = "sortKey"
  const val SORT_KEY_VALUE = "sortKeyVal"

  const val CUSTOMER_ID_1 = "customer1"
  const val CUSTOMER_ID_2 = "customer2"

  const val WAIT_TIME_BETWEEN_DDB_OPERATIONS_MS = 500L
}
