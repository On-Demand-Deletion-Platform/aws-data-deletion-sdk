# aws-data-deletion-sdk

[![Local tests](https://github.com/On-Demand-Deletion-Platform/aws-data-deletion-sdk/actions/workflows/test.yml/badge.svg)](https://github.com/On-Demand-Deletion-Platform/aws-data-deletion-sdk/actions/workflows/test.yml) [![Deploy Documentation](https://github.com/On-Demand-Deletion-Platform/aws-data-deletion-sdk/actions/workflows/docs.yml/badge.svg)](https://github.com/On-Demand-Deletion-Platform/aws-data-deletion-sdk/actions/workflows/docs.yml)

Kotlin library with connectors and data models for executing data deletion requests against onboarded AWS-hosted data stores.

See https://on-demand-deletion-platform.github.io/aws-data-deletion-sdk for SDK documentation.

## Usage

### DynamoDB deletion requests

The SDK supports three deletion strategies for DynamoDB tables:

#### 1. Table Key Deletion (`TABLE_KEY`)
Delete items by primary key (partition key and optional sort key).

```kotlin
val deletionTarget = DynamoDbDeletionTarget(
    strategy = DynamoDbDeletionStrategyType.TABLE_KEY,
    awsRegion = "us-east-1",
    tableName = "customers",
    partitionKeyName = "serviceId",
    sortKeyName = "customerId", // Optional
    deletionKeySchema = DynamoDbDeletionKeySchema(
        primaryKeyName = "serviceId",
        secondaryKeyName = "customerId" // Optional
    )
)

val deletionKey = DynamoDbDeletionKeyValue(
    primaryKeyValue = "service-123",
    secondaryKeyValue = "customer-123"
)

DynamoDbDeletionConnector(dynamoDbClient).deleteData(deletionTarget, deletionKey)
```

#### 2. GSI Query Deletion (`GSI_QUERY`)
Query a Global Secondary Index and delete all matching items.

```kotlin
val deletionTarget = DynamoDbDeletionTarget(
    strategy = DynamoDbDeletionStrategyType.GSI_QUERY,
    awsRegion = "us-east-1",
    tableName = "purchases",
    partitionKeyName = "purchaseId", // Table partition key
    sortKeyName = "customerId", // Table sort key (optional)
    gsiName = "customer-index",
    deletionKeySchema = DynamoDbDeletionKeySchema(
        primaryKeyName = "customerId", // GSI partition key
        secondaryKeyName = "purchaseId" // GSI sort key (optional)
    )
)

val deletionKey = DynamoDbDeletionKeyValue(
    primaryKeyValue = "customer-123",
    secondaryKeyValue = "0510511e-49d2-4753-8aca-a3ddfc99513b" // Optional
)

DynamoDbDeletionConnector(dynamoDbClient).deleteData(deletionTarget, deletionKey)
```

#### 3. Scan Deletion (`SCAN`)
Scan the entire table and delete items matching specified attributes.

```kotlin
val deletionTarget = DynamoDbDeletionTarget(
    strategy = DynamoDbDeletionStrategyType.SCAN,
    awsRegion = "us-east-1",
    tableName = "purchases",
    partitionKeyName = "purchaseId", // Table partition key
    sortKeyName = "timestamp", // Table sort key (optional)
    deletionKeySchema = DynamoDbDeletionKeySchema(
        primaryKeyName = "tenantId", // Attribute to scan for
        secondaryKeyName = "customerId" // Secondary attribute filter (optional)
    )
)

val deletionKey = DynamoDbDeletionKeyValue(
    primaryKeyValue = "tenant-123",
    secondaryKeyValue = "customer-123" // Optional
)

DynamoDbDeletionConnector(dynamoDbClient).deleteData(deletionTarget, deletionKey)
```

### S3 deletion requests

The SDK supports two deletion strategies for S3 buckets:

#### 1. Object Key Deletion (`OBJECT_KEY`)
Delete S3 objects by matching object key patterns.

```kotlin
val deletionTarget = S3DeletionTarget(
    strategy = S3DeletionStrategyType.OBJECT_KEY,
    awsRegion = "us-east-1",
    bucketName = "purchases",
    objectKeyPrefix = "data/customers/", // Optional S3 key prefix for efficient search
    deletionKeyPattern = Pattern.compile("data/customers/([\\w\\-]+)/.*") // Pattern with exactly one capture group
)

val deletionKey = S3DeletionKeyValue(
    deletionKeyPatternCaptureValue = "customer-123" // Matches capture group in pattern
)

S3DeletionConnector(s3Client).deleteData(deletionTarget, deletionKey)
```

#### 2. Row Level Deletion (`ROW_LEVEL`)
(NOT YET IMPLEMENTED)

Remove specific rows from S3 files containing data from multiple customers.

```kotlin
val deletionTarget = S3DeletionTarget(
    strategy = S3DeletionStrategyType.ROW_LEVEL,
    awsRegion = "us-east-1",
    bucketName = "purchase-events",
    objectKeyPrefix = "data/events/", // Optional S3 key prefix for efficient search
    deletionKeyPattern = Pattern.compile("data/events/([\\w\\-]+)/.*"), // Pattern with capture group
    deletionRowAttributeName = "customerId", // Attribute to filter rows by
    objectFileFormat = FileFormat.JSONL // Supported: JSONL, PARQUET
)

val deletionKey = S3DeletionKeyValue(
    deletionKeyPatternCaptureValue = "customer-123", // Matches capture group in pattern
    deletionRowAttributeValue = "customer-123" // Value to match in row attribute
)

S3DeletionConnector(s3Client).deleteData(deletionTarget, deletionKey)
```

## Importing

### Prerequisites

Follow [GitHub's "Managing your personal access tokens" guide](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens) to set up a personal access token that has repo and read:packages permissions (required to access public repositories and download packages from GitHub Package Registry).

Set up a `GITHUB_USERNAME` environment variable matching your GitHub username.

Set up a `GITHUB_TOKEN` environment variable matching your GitHub personal access token.

### Gradle Kotlin DSL

Merge the following into your build.gradle.kts file:

```kotlin
repositories {
    // GitHub Packages repository for aws-data-deletion-sdk
    maven {
        url = uri("https://maven.pkg.github.com/On-Demand-Deletion-Platform/aws-data-deletion-sdk")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    // AWS Data Deletion SDK
    implementation("com.ondemanddeletionplatform:aws-data-deletion-sdk:0.0.2")
}
```

### Gradle Groovy

Merge the following into your build.gradle file:

```groovy
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/On-Demand-Deletion-Platform/aws-data-deletion-sdk")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation 'com.ondemanddeletionplatform:aws-data-deletion-sdk:0.0.2'
}
```

### Maven

Merge the following into your pom.xml file:

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/On-Demand-Deletion-Platform/aws-data-deletion-sdk</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.ondemanddeletionplatform</groupId>
        <artifactId>aws-data-deletion-sdk</artifactId>
        <version>0.0.2</version>
    </dependency>
</dependencies>
```

## Technologies

* [AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/api/latest/) is used for AWS integrations for deleting data from AWS data stores such as DynamoDB.
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) and [Testcontainers](https://testcontainers.com/) are used to host short-lived databases such as DynamoDB for use in lightweight integ tests.
* [Dokka](https://kotlinlang.org/docs/dokka-introduction.html) is used to auto-generate local library documentation for developers.
* [GitHub Actions](https://docs.github.com/en/actions) are used to automatically build and run unit tests and local integ tests against service code changes pushed or submitted through pull requests.
* [Gradle](https://docs.gradle.org) is used to build the project and manage package dependencies.
* [Kotlin](https://kotlinlang.org/) is used as the programming language for this service.

## License
The code in this project is released under the [GPL-3.0 License](LICENSE).
