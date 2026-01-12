# aws-data-deletion-sdk

[![Local tests](https://github.com/On-Demand-Deletion-Platform/aws-data-deletion-sdk/actions/workflows/test.yml/badge.svg)](https://github.com/On-Demand-Deletion-Platform/aws-data-deletion-sdk/actions/workflows/test.yml) [![Deploy Documentation](https://github.com/On-Demand-Deletion-Platform/aws-data-deletion-sdk/actions/workflows/docs.yml/badge.svg)](https://github.com/On-Demand-Deletion-Platform/aws-data-deletion-sdk/actions/workflows/docs.yml)

Kotlin library with connectors and data models for executing data deletion requests against onboarded AWS-hosted data stores.

See https://on-demand-deletion-platform.github.io/aws-data-deletion-sdk for SDK documentation.

## Importing

### Prerequisites

Follow [GitHub's "Managing your personal access tokens" guide](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens) to set up a personal access token that has repo and read:packages permissions (required to access public repositories and download packages from GitHub Package Registry).

Set up a `GITHUB_USERNAME` environment variable matching your GitHub username.

Set up a `GITHUB_TOKEN` environment variable matching your GitHub personal access token.

### Gradle (build.gradle.kts)

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

## Technologies

* [AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/api/latest/) is used for AWS integrations for deleting data from AWS data stores such as DynamoDB.
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) and [Testcontainers](https://testcontainers.com/) are used to host short-lived databases such as DynamoDB for use in lightweight integ tests.
* [Dokka](https://kotlinlang.org/docs/dokka-introduction.html) is used to auto-generate local library documentation for developers.
* [GitHub Actions](https://docs.github.com/en/actions) are used to automatically build and run unit tests and local integ tests against service code changes pushed or submitted through pull requests.
* [Gradle](https://docs.gradle.org) is used to build the project and manage package dependencies.
* [Kotlin](https://kotlinlang.org/) is used as the programming language for this service.

## License
The code in this project is released under the [GPL-3.0 License](LICENSE).
