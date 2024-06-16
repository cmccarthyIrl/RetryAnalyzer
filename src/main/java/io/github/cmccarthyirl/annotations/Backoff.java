package io.github.cmccarthyirl.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Backoff {
    long delay() default 1000;
}

