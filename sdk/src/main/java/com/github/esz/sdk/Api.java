package com.github.esz.sdk;

import java.lang.annotation.*;

/**
 * Created by shaoaq on 7/15/15.
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Api {
    String value() default "";
}
