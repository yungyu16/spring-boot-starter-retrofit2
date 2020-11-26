package com.github.yungyu16.spring.http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CreatedDate: 2020/11/25
 * Author: songjialin
 */
@Configuration
public class RetrofitClientAutoConfiguration {

    @Bean
    public HttpClientProxyFactory httpClientProxyFactory() {
        return new HttpClientProxyFactory();
    }
}
