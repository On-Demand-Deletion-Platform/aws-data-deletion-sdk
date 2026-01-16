package com.ondemanddeletionplatform.deletion.connectors.dynamodb

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.QueryRequest
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionKeyValue
import com.ondemanddeletionplatform.deletion.models.dynamodb.DynamoDbDeletionTarget
import com.ondemanddeletionplatform.deletion.models.dynamodb.ValidatedDynamoDbGsiDeletionTarget

/**
 * DynamoDB on-demand-deletion strategy that deletes items based on GSI key.
 */
class DynamoDbGsiKeyDeletionStrategy : DynamoDbDeletionStrategy() {
  override suspend fun deleteData(ddb: DynamoDbClient, deletionTarget: DynamoDbDeletionTarget, deletionKey: DynamoDbDeletionKeyValue) {
    println("Called GSI deletion for deletionKey: $deletionKey")
    val gsiDeletionTarget = ValidatedDynamoDbGsiDeletionTarget.fromDeletionTarget(deletionTarget)

    val gsiQueryAttrNames = constructGsiQueryAttrNames(gsiDeletionTarget)
    val gsiQueryAttrValues = constructGsiQueryAttrValues(deletionKey)
    val gsiKeyConditionExpr = constructGsiKeyConditionExpr(gsiDeletionTarget)

    queryAndDeleteGsiItems(ddb, gsiDeletionTarget, gsiQueryAttrNames, gsiQueryAttrValues, gsiKeyConditionExpr)
  }

  private fun constructGsiQueryAttrNames(deletionTarget: ValidatedDynamoDbGsiDeletionTarget): Map<String, String> {
    val attrNames = mutableMapOf<String, String>()
    attrNames["#partitionKeyAlias"] = deletionTarget.deletionKeySchema.primaryKeyName
    if (deletionTarget.deletionKeySchema.secondaryKeyName != null) {
      attrNames["#sortKeyAlias"] = deletionTarget.deletionKeySchema.secondaryKeyName
    }
    return attrNames
  }

  private fun constructGsiQueryAttrValues(deletionKey: DynamoDbDeletionKeyValue): Map<String, AttributeValue> {
    val attrValues = mutableMapOf<String, AttributeValue>()
    attrValues[":partitionKeyValue"] = AttributeValue.S(deletionKey.primaryKeyValue)
    if (deletionKey.secondaryKeyValue != null) {
      attrValues[":sortKeyValue"] = AttributeValue.S(deletionKey.secondaryKeyValue)
    }
    return attrValues
  }

  private fun constructGsiKeyConditionExpr(deletionTarget: ValidatedDynamoDbGsiDeletionTarget): String {
    var keyConditionExpr = "#partitionKeyAlias = :partitionKeyValue"
    if (deletionTarget.deletionKeySchema.secondaryKeyName != null) {
      keyConditionExpr += " AND #sortKeyAlias = :sortKeyValue"
    }
    return keyConditionExpr
  }

  private suspend fun queryAndDeleteGsiItems(
    ddb: DynamoDbClient,
    deletionTarget: ValidatedDynamoDbGsiDeletionTarget,
    attrNames: Map<String, String>,
    attrValues: Map<String, AttributeValue>,
    keyConditionExpr: String
  ) {
    var lastKey: Map<String, AttributeValue>? = null
    do {
      val gsiQueryRequest = QueryRequest {
        tableName = deletionTarget.tableName
        indexName = deletionTarget.gsiName
        expressionAttributeNames = attrNames
        expressionAttributeValues = attrValues
        keyConditionExpression = keyConditionExpr
        exclusiveStartKey = lastKey
      }

      val response = ddb.query(gsiQueryRequest)
      response.items?.forEach {
        val tableKey = mutableMapOf<String, AttributeValue>()
        val partitionKeyValue = it[deletionTarget.partitionKeyName]?.asSOrNull()
        checkNotNull(partitionKeyValue) {
          "GSI query result partition key missing or not a string: $partitionKeyValue"
        }
        tableKey[deletionTarget.partitionKeyName] = AttributeValue.S(partitionKeyValue)
        if (deletionTarget.sortKeyName != null) {
          val sortKeyValue = it[deletionTarget.sortKeyName]?.asSOrNull()
          checkNotNull(sortKeyValue) {
            "GSI query result sort key missing or not a string: $sortKeyValue"
          }
          tableKey[deletionTarget.sortKeyName] = AttributeValue.S(sortKeyValue)
        }

        deleteDataByKey(ddb, deletionTarget.tableName, tableKey)
      }
      lastKey = response.lastEvaluatedKey
    } while (!lastKey.isNullOrEmpty())
  }
}
