package com.github.yungyu16.spring.http.annotion;

import com.github.yungyu16.spring.stub.annotation.ProxyStubScan;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * CreatedDate: 2020/9/25
 * Author: songjialin
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ProxyStubScan(markAnnotation = HttpClient.class)
public @interface HttpClientScan {
    @AliasFor(value = "value", annotation = ProxyStubScan.class)
    String[] value() default {};

    @AliasFor(value = "basePackages", annotation = ProxyStubScan.class)
    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};
}
