package com.ondemanddeletionplatform.deletion.models.dynamodb

/**
 * Common data model for on-demand deletion strategies for DynamoDB tables.
 *
 * @property strategy The deletion strategy to use for this target
 * @property awsRegion AWS region where the DynamoDB table is located
 * @property tableName Name of the DynamoDB table to delete data from
 * @property partitionKeyName Name of the table's partition key attribute
 * @property sortKeyName Name of the table's sort key attribute. Optional.
 * @property deletionKeySchema Schema defining the key structure for deletion operations
 * @property gsiName Name of the Global Secondary Index to query. Required for GSI_QUERY strategy.
 */
data class DynamoDbDeletionTarget(
  /**
   * The deletion strategy to use for this target.
   */
  val strategy: DynamoDbDeletionStrategyType,

  /**
   * AWS region where the DynamoDB table is located.
   */
  val awsRegion: String,

  /**
   * Name of the DynamoDB table to delete data from.
   */
  val tableName: String,

  /**
   * Name of the table's partition key attribute.
   */
  val partitionKeyName: String,

  /**
   * Name of the table's sort key attribute. Optional.
   */
  val sortKeyName: String? = null,

  /**
   * Schema defining the key structure for deletion operations.
   */
  val deletionKeySchema: DynamoDbDeletionKeySchema,

  /**
   * Name of the Global Secondary Index to query.
   * Required for GSI_QUERY strategy, ignored for other strategies.
   */
  val gsiName: String? = null
)
