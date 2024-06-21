package io.github.cmccarthyirl.retry;

import io.github.cmccarthyirl.annotations.Retryable;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Aspect for handling retry logic on methods annotated with {@link Retryable}.
 */
@Aspect
public class RetryAspect {

    /**
     * Pointcut that matches methods annotated with {@link Retryable}.
     *
     * @param retryable the {@link Retryable} annotation instance
     */
    @Pointcut("@annotation(retryable) && execution(* *(..))")
    public void retryableMethods(Retryable retryable) {
    }

    /**
     * Around advice that retries the method execution based on the {@link Retryable} annotation.
     *
     * @param joinPoint the join point representing the method execution
     * @param retryable the {@link Retryable} annotation instance
     * @return the result of the method execution
     * @throws Throwable if the method execution fails after the maximum retry attempts
     */
    @Around(value = "retryableMethods(retryable)", argNames = "joinPoint,retryable")
    public Object retryMethod(ProceedingJoinPoint joinPoint, Retryable retryable) throws Throwable {
        int maxAttempts = retryable.maxAttempts(); // Maximum number of retry attempts
        long delay = retryable.backoff().delay(); // Delay between retries in milliseconds
        double multiplier = retryable.backoff().multiplier(); // Multiplier for backoff
        Class<? extends Throwable>[] retryExceptions = retryable.include(); // Exceptions to retry on

        Throwable lastException = null; // Store the last exception encountered
        int attempts = 0;
        // Retry logic loop
        while (attempts < maxAttempts) {
            try {
                return joinPoint.proceed();
            } catch (Throwable ex) {
                lastException = ex;
                if (!isExceptionInIncludedList(ex, retryExceptions)) {
                    throw ex;
                }
                attempts++;
                if (attempts < maxAttempts) {
                    Thread.sleep(delay);
                    delay *= multiplier; // Increase delay according to multiplier
                }
            }
        }
        throw lastException != null ? lastException : new RuntimeException("Retry failed after max attempts");
    }

    /**
     * Helper method to check if an exception is in the included classes for retry.
     *
     * @param ex              the exception to check
     * @param includedClasses the array of exception classes to include
     * @return true if the exception is in the included classes, false otherwise
     */
    private boolean isExceptionInIncludedList(Throwable ex, Class<? extends Throwable>[] includedClasses) {
        for (Class<? extends Throwable> includedClass : includedClasses) {
            if (includedClass.isInstance(ex)) {
                return true;
            }
        }
        return false;
    }
}
