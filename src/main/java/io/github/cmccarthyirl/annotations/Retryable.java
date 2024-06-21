package io.github.cmccarthyirl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate that a method or type is retryable.
 * <p>
 * This annotation specifies the maximum number of retry attempts and the
 * backoff strategy to use in case of failure. It also allows specifying
 * which exceptions should trigger a retry.
 * <p>
 * Example usage:
 * <p>
 * {@code @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 2000, multiplier = 2.0), include = {IOException.class, SQLException.class})}
 * <p>
 * Attributes:
 * - `maxAttempts`: Maximum number of retry attempts.
 * - `backoff`: Backoff strategy to use between retries.
 * - `include`: Array of exception classes that should trigger a retry.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Retryable {
    /**
     * The maximum number of retry attempts.
     *
     * @return the maximum number of attempts
     */
    int maxAttempts() default 3;

    /**
     * The backoff strategy to use between retry attempts.
     *
     * @return the backoff strategy
     */
    Backoff backoff() default @Backoff;

    /**
     * The exceptions that should trigger a retry.
     *
     * @return array of exception classes to include
     */
    Class<? extends Throwable>[] include();
}