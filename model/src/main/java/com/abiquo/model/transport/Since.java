package com.abiquo.model.transport;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Since
{
    String version();

    String desc() default "";
}
