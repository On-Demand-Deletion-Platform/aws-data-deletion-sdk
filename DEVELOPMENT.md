# Developer Guide
## Building the project

### First-time set-up
To build the project for the first time, run

```sh
./gradlew build
```

and validate that it completes successfully.

### Subsequent builds
In order to clean up stale build artifacts and rebuild the API models based on your latest changes, run

```sh
./gradlew clean build
```

If you do not clean before building, your local environment may continue to use stale, cached artifacts in builds.

### Running local integ tests
Prerequisite set-up:
1. Install Docker Desktop from https://www.docker.com/products/docker-desktop/
2. Launch Docker Desktop and create or sign into a Docker account

To run local integ tests against test containers, run

```sh
./gradlew localIntegTest -DrunLocalIntegTests=true
```

If need to debug errors, add the --info option to the command.

If the integ tests automatically pass with the message `Configuration cache entry reused.`, add the --rerun-tasks option to the command.

## Helpful commands

* `./gradlew build` - build project, run lint checker, and run unit tests
* `./gradlew clean build` - clear build artifacts, rebuild project, run lint checker, and run unit tests
* `./gradlew detekt` - run lint checker
* `./gradlew run` - run the application
* `./gradlew tasks` - list available Gradle tasks
* `./gradlew test` - run unit tests
* `./gradlew test --tests TestClass --info` - run unit tests from a specific test class with info-level logging, helpful when debugging errors
* `./gradlew test --tests TestClass.TestMethod --info` - run a specific unit test with info-level logging, helpful when debugging errors

## Troubleshooting

#### My local tests failed but the output doesn't include logs or stack traces needed to debug

Run `./gradlew build --info` to rerun the tests with info logging enabled, which will include logs and stack traces for failed tests.

#### My local builds are not picking up Gradle dependency changes

Run `./gradlew clean build --refresh-dependencies` to ignore your Gradle environment's cached entries for modules and artifacts, and download new versions if they have different published hashsums.

## Resources

* [AWS SDK for Kotlin API docs](https://docs.aws.amazon.com/sdk-for-kotlin/api/latest/)
* [AWS SDK for Kotlin DynamoDB code examples](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/kotlin_dynamodb_code_examples.html)
