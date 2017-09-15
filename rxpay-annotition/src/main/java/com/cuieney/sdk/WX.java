package com.cuieney.sdk;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * find package name
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface WX {
    /**
     * When registered appid package name
     * @return
     */
    String packageName();
}