package com.github.yungyu16.spring.http.annotion;

import java.lang.annotation.*;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2020/9/26.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestTrack {
    String topic() default "";

    String[] tags() default {};
}
