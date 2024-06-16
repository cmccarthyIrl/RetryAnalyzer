package com.cmccarthy.retry;

import com.cmccarthy.annotations.Retryable;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class RetryAspect {

    // Define pointcut for methods annotated with @Retryable
    @Pointcut("@annotation(retryable) && execution(* *(..))")
    public void retryableMethods(Retryable retryable) {
        // This method is just a pointcut definition, no need for additional logic here.
    }

    // Advice that wraps around the annotated method to retry on failure
    @Around(value = "retryableMethods(retryable)", argNames = "joinPoint,retryable")
    public Object retryMethod(ProceedingJoinPoint joinPoint, Retryable retryable) throws Throwable {
        int maxAttempts = retryable.maxAttempts(); // Maximum number of retry attempts
        long delay = retryable.backoff().delay(); // Delay between retries in milliseconds
        Class<? extends Throwable>[] retryExceptions = retryable.include(); // Exceptions to retry on

        Throwable lastException = null; // Store the last exception encountered

        // Retry logic loop using a for loop for better readability
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                // Proceed with the original method invocation
                return joinPoint.proceed();
            } catch (Throwable ex) {
                // Check if the exception should be retried based on included exception classes
                if (!isExceptionInIncludedList(ex, retryExceptions)) {
                    throw ex; // If not included, propagate the exception
                }
                lastException = ex; // Store the last encountered exception
                // If attempts are less than maxAttempts, apply delay before retrying
                if (attempt < maxAttempts) {
                    Thread.sleep(delay);
                }
            }
        }
        // If retries exhausted, throw a new RuntimeException with the last encountered exception
        throw new RuntimeException("Retry failed after " + maxAttempts + " attempts", lastException);
    }

    // Helper method to check if an exception is in the included classes for retry
    private boolean isExceptionInIncludedList(Throwable ex, Class<? extends Throwable>[] includedClasses) {
        for (Class<? extends Throwable> includedClass : includedClasses) {
            if (includedClass.isInstance(ex)) {
                return true;
            }
        }
        return false;
    }
}
