package com.ondemanddeletionplatform.deletion.connectors.dynamodb

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.ScanRequest
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionKeyValue
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionTarget
import com.ondemanddeletionplatform.deletion.models.dynamodb.ValidatedDynamoDbScanDeletionTarget

/**
 * DynamoDB on-demand-deletion strategy that scans the table
 * and deletes all items matching deletion key attributes.
 *
 * Note: This strategy is highly inefficient and should be used
 * only as a last resort when it is not possible to index the
 * table by deletion key.
 */
class DynamoDbScanDeletionStrategy : DynamoDbDeletionStrategy() {
  override suspend fun deleteData(ddb: DynamoDbClient, deletionTarget: DynamoDbDeletionTarget, deletionKey: DynamoDbDeletionKeyValue) {
    println("Called scan deletion for deletionKey: $deletionKey")
    val scanDeletionTarget = ValidatedDynamoDbScanDeletionTarget.fromDeletionTarget(deletionTarget)
    val hasValidSecondaryKey = checkHasValidSecondaryKey(scanDeletionTarget, deletionKey)

    val filterExpression = constructFilterExpression(hasValidSecondaryKey)
    val expressionAttributeNames = constructAttributeNames(scanDeletionTarget, hasValidSecondaryKey)
    val expressionAttributeValues = constructAttributeValues(deletionKey)

    var lastKey: Map<String, AttributeValue>? = null
    do {
      val scanRequest = ScanRequest {
        tableName = scanDeletionTarget.tableName
        this.filterExpression = filterExpression
        this.expressionAttributeNames = expressionAttributeNames
        this.expressionAttributeValues = expressionAttributeValues
        exclusiveStartKey = lastKey
      }

      val response = ddb.scan(scanRequest)
      response.items?.forEach { item ->
        val tableKey = mutableMapOf<String, AttributeValue>()
        val partitionKeyValue = item[scanDeletionTarget.partitionKeyName]?.asSOrNull()
        checkNotNull(partitionKeyValue) {
          "Scan result partition key missing or not a string: $partitionKeyValue"
        }
        tableKey[scanDeletionTarget.partitionKeyName] = AttributeValue.S(partitionKeyValue)
        if (scanDeletionTarget.sortKeyName != null) {
          val sortKeyValue = item[scanDeletionTarget.sortKeyName]?.asSOrNull()
          checkNotNull(sortKeyValue) {
            "Scan result sort key missing or not a string: $sortKeyValue"
          }
          tableKey[scanDeletionTarget.sortKeyName] = AttributeValue.S(sortKeyValue)
        }

        deleteDataByKey(ddb, scanDeletionTarget.tableName, tableKey)
      }
      lastKey = response.lastEvaluatedKey
    } while (!lastKey.isNullOrEmpty())
  }

  private fun checkHasValidSecondaryKey(
    scanDeletionTarget: ValidatedDynamoDbScanDeletionTarget,
    deletionKey: DynamoDbDeletionKeyValue
  ): Boolean {
    if (scanDeletionTarget.deletionKeySchema.secondaryKeyName != null) {
      requireNotNull(deletionKey.secondaryKeyValue) {
        "Mismatch between deletion key and deletion key schema ${scanDeletionTarget.deletionKeySchema}, missing secondary key value"
      }
      return true
    }
    return false
  }

  private fun constructFilterExpression(hasSecondaryKey: Boolean): String {
    var expr = "#primaryKey = :primaryKeyValue"
    if (hasSecondaryKey) {
      expr += " AND #secondaryKey = :secondaryKeyValue"
    }
    return expr
  }

  private fun constructAttributeNames(
    scanDeletionTarget: ValidatedDynamoDbScanDeletionTarget,
    hasSecondaryKey: Boolean
  ): Map<String, String> {
    val names = mutableMapOf("#primaryKey" to scanDeletionTarget.deletionKeySchema.primaryKeyName)
    if (hasSecondaryKey) {
      names["#secondaryKey"] = scanDeletionTarget.deletionKeySchema.secondaryKeyName!!
    }
    return names
  }

  private fun constructAttributeValues(deletionKey: DynamoDbDeletionKeyValue): Map<String, AttributeValue> {
    val values = mutableMapOf(":primaryKeyValue" to AttributeValue.S(deletionKey.primaryKeyValue))
    if (deletionKey.secondaryKeyValue != null) {
      values[":secondaryKeyValue"] = AttributeValue.S(deletionKey.secondaryKeyValue)
    }
    return values
  }
}
