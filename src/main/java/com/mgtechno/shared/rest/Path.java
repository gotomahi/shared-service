package com.mgtechno.shared.rest;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Path {
    String value();
    HttpMethod method() default HttpMethod.ANY;
}
