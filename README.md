# RetryAspect

This `RetryAspect` class is an implementation of the retry mechanism in Java using Aspect-Oriented Programming (AOP). It allows methods annotated with `@Retryable` to be executed multiple times in case of failure, with configurable retry attempts and backoff delay.

## Features

- **Retry Mechanism**: Automatically retries a method execution if it fails, based on the configuration provided by the `@Retryable` annotation.
- **Configurable Attempts and Delay**: Allows configuration of the maximum number of retry attempts and the delay between retries.
- **Exception Handling**: Only retries for specified exceptions, providing fine-grained control over which failures should trigger a retry.

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
In this example, the unreliableMethod will be retried up to 5 times with a delay of 2000 milliseconds between retries, but only if an IOException is thrown.

## Class Details RetryAspect Class
The RetryAspect class contains the AOP logic for handling retries.

```java

@Aspect
public class RetryAspect {

    @Pointcut("@annotation(retryable) && execution(* *(..))")
    public void retryableMethods(Retryable retryable) {
    }

    @Around(value = "retryableMethods(retryable)", argNames = "joinPoint,retryable")
    public Object retryMethod(ProceedingJoinPoint joinPoint, Retryable retryable) throws Throwable {
        int maxAttempts = retryable.maxAttempts();
        long delay = retryable.backoff().delay();
        int attempts = 0;
        Throwable lastException = null;

        while (attempts < maxAttempts) {
            try {
                return joinPoint.proceed();
            } catch (Throwable ex) {
                lastException = ex;
                if (!isExceptionIncluded(ex, retryable.include())) {
                    throw ex;
                }
                attempts++;
                if (attempts < maxAttempts) {
                    Thread.sleep(delay);
                }
            }
        }
        throw lastException != null ? lastException : new RuntimeException("Retry failed after max attempts");
    }

    private boolean isExceptionIncluded(Throwable ex, Class<? extends Throwable>[] includedClasses) {
        for (Class<? extends Throwable> includedClass : includedClasses) {
            if (includedClass.isInstance(ex)) {
                return true;
            }
        }
        return false;
    }
}
```

### Methods
- **retryableMethods**: Pointcut definition for methods annotated with `@Retryable`.
- **retryMethod**: Around advice that handles the retry logic.
- **isExceptionIncluded**: Helper method to check if an exception is included in the list of retryable exceptions.

## Dependencies
Ensure that you have the necessary dependencies for AspectJ in your project

```xml
    <dependencies>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjrt</artifactId>
            <version>1.9.7</version>
        </dependency>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.9.7</version>
        </dependency>
    </dependencies>
    <plugins>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>aspectj-maven-plugin</artifactId>
            <version>1.15.0</version>
        </plugin>
    </plugins>

```

## Contribute
Contributions are welcome! [Here's](https://github.com/cmccarthyIrl/RetryAnalyzer/blob/master/CONTRIBUTING.md) how you can get started!!

## License
This project is licensed under the MIT License - see the [LICENSE](https://github.com/cmccarthyIrl/RetryAnalyzer/blob/master/LICENSE) file for details.