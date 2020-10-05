package com.github.yungyu16.spring.retrofit.annotion;

import java.lang.annotation.*;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2020/9/26.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestTimeout {
    /**
     * 连接超时
     * -1=默认值
     * 单位秒
     *
     * @return
     */
    int connectTimeout() default -1;

    /**
     * 读超时
     * -1=默认值
     * 单位秒
     *
     * @return
     */
    int readTimeout() default -1;

    /**
     * 写超时
     * -1=默认值
     * 单位秒
     *
     * @return
     */
    int writeTimeout() default -1;
}
