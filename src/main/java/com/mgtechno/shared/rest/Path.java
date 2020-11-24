package com.mgtechno.shared.rest;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface Path {
    String value();
    HttpMethod method() default HttpMethod.GET;
}
