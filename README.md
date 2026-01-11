# aws-data-deletion-worker

[![Local tests](https://github.com/On-Demand-Deletion-Platform/aws-data-deletion-worker/actions/workflows/test.yml/badge.svg)](https://github.com/On-Demand-Deletion-Platform/aws-data-deletion-worker/actions/workflows/test.yml)

Service code for processing a deletion request and deleting a customer's data from onboarded AWS-hosted data stores.

## Technologies

* [AWS SDK for Kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/api/latest/) is used for AWS integrations for deleting data from AWS data stores such as DynamoDB.
* [GitHub Actions](https://docs.github.com/en/actions) are used to automatically build and run unit tests and local integ tests against service code changes pushed or submitted through pull requests.
* [Gradle](https://docs.gradle.org) is used to build the project and manage package dependencies.

## License
The code in this project is released under the [GPL-3.0 License](LICENSE).
