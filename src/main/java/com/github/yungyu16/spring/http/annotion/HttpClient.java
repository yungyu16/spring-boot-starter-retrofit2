package com.github.yungyu16.spring.http.annotion;

import com.github.yungyu16.spring.http.HttpClientProxyFactory;
import com.github.yungyu16.spring.stub.annotation.ProxyStub;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * CreatedDate: 2020/9/25
 * Author: songjialin
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ProxyStub(factoryType = HttpClientProxyFactory.class)
public @interface HttpClient {
    @AliasFor("value")
    String baseUrl() default "";

    @AliasFor("baseUrl")
    String value() default "";
}
