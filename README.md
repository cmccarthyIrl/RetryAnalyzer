# RetryAspect

This `RetryAspect` class is an implementation of the retry mechanism in Java using Aspect-Oriented Programming (AOP). It
allows methods annotated with `@Retryable` to be executed multiple times in case of failure, with configurable retry
attempts and backoff delay.

## Features

- **Retry Mechanism**: Automatically retries a method execution if it fails, based on the configuration provided by
  the `@Retryable` annotation.
- **Configurable Attempts and Delay**: Allows configuration of the maximum number of retry attempts and the delay
  between retries.
- **Exception Handling**: Only retries for specified exceptions, providing fine-grained control over which failures
  should trigger a retry.

## Usage

To use the `RetryAspect` class, follow these steps:

### 1. Annotate Methods with `@Retryable`

Annotate the methods you want to be retryable with the `@Retryable` annotation.

```java
public class MyService {
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000), include = {RuntimeException.class})
    public void performTask() {
        // Method implementation
    }
}
```

In this example, the unreliableMethod will be retried up to 3 times with a delay of 1000 milliseconds between retries,
but only if an RuntimeException is thrown.

### 2. Configure the `@Retryable` Annotation

The `@Retryable` annotation allows you to configure the retry behavior for a method. It has the following attributes:

- **maxAttempts**: The maximum number of retry attempts (default is 3).
- **backoff**: Specifies the backoff policy, including the delay between retries.
- **include**: The exceptions that should trigger a retry.

```java

@Retryable(maxAttempts = 5, backoff = @Backoff(delay = 2000), include = {IOException.class})
public void unreliableMethod() {
// Method implementation
}
```

In this example, the unreliableMethod will be retried up to 5 times with a delay of 2000 milliseconds between retries,
but only if an IOException is thrown.

## Usage

To use the RetryAspect library in your project, include the following dependencies and plugins in your `pom.xml` file:

1. Add `retryaspect` as a dependency

```xml

<dependencies>
    ...
    <dependency>
        <groupId>io.github.cmccarthyirl</groupId>
        <artifactId>retryaspect</artifactId>
        <version>1.0.1</version>
    </dependency>
    ...
</dependencies>
```

2. Configure the AspectJ Maven Plugin

```xml

<build>
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>aspectj-maven-plugin</artifactId>
            <version>1.15.0</version>
            <configuration>
                <complianceLevel>16.0</complianceLevel>
                <source>16.0</source>
                <target>16.0</target>
                <showWeaveInfo>true</showWeaveInfo>
                <verbose>true</verbose>
                <Xlint>ignore</Xlint>
                <encoding>UTF-8</encoding>
                <aspectLibraries>
                    <aspectLibrary>
                        <groupId>io.github.cmccarthyirl</groupId>
                        <artifactId>retryaspect</artifactId>
                    </aspectLibrary>
                </aspectLibraries>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <!-- use this goal to weave all your main classes -->
                        <goal>compile</goal>
                        <!-- use this goal to weave all your test classes -->
                        <goal>test-compile</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

## Contribute

Contributions are welcome! [Here's](https://github.com/cmccarthyIrl/RetryAnalyzer/blob/master/CONTRIBUTING.md) how you can get started!!

## License

This project is licensed under the MIT License - see the [LICENSE](https://github.com/cmccarthyIrl/RetryAnalyzer/blob/master/LICENSE) file for details.




