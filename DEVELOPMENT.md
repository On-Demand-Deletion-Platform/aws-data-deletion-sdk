# Developer Guide
## Building the project

### First-time set-up
To build the project for the first time, run

```sh
./gradlew build
```

and validate that it completes successfully.

You can then browse auto-generated library docs by opening build/dokka/html/index.html in a browser.

### Subsequent builds
In order to clean up stale build artifacts and rebuild the API models based on your latest changes, run

```sh
./gradlew clean build
```

If you do not clean before building, your local environment may continue to use stale, cached artifacts in builds.

## Running local integ tests
Prerequisite set-up:
1. Install Docker Desktop from https://www.docker.com/products/docker-desktop/
2. Launch Docker Desktop and create or sign into a Docker account

To run local integ tests against test containers, run

```sh
./gradlew localIntegTest -DrunLocalIntegTests=true
```

### Debugging local integ tests

Helpful localIntegTest options:
* `--info` - print info logs to console
* `--rerun-tasks` - force rerun even if prior integ test run, use if auto-completes with message `Configuration cache entry reused.`

Integ test code snippets to help with debugging:
* Can use `localstack.printLogs()` to print container logs for debugging.
* If need to debug LocalStack startup issues, update the `LocalStackContainer` definition to include:
  * `.withEnv("LS_LOG", "trace") // Enable detailed logging for debugging`
  * `.withEnv("DEBUG", "1") // Enable debug mode`

## Publishing release artifacts to GitHub Packages
Follow [GitHub's "Managing your personal access tokens" guide](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens) to set up a personal access token that has write permissions to your GitHub Packages.

Set up a `GITHUB_USERNAME` environment variable matching your GitHub username.

Set up a `GITHUB_TOKEN` environment variable matching your GitHub personal access token.

Increment the package's Maven package version in `build.gradle.kts`.

Run `./gradlew clean build` to ensure build artifacts are based on the latest Smithy models.

Run `./gradlew publish` to publish the new GitHub Package version.

Ref: https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#authenticating-to-github-packages

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
* [GitHub Actions docs](https://docs.github.com/en/actions)
* [Kotlin API docs](https://kotlinlang.org/docs/api-references.html)
* [Testcontainers quickstart guide for JUnit 5](https://java.testcontainers.org/quickstart/junit_5_quickstart/)
