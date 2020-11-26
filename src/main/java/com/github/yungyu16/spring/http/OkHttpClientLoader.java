package com.github.yungyu16.spring.http;

import okhttp3.OkHttpClient;

/**
 * CreatedDate: 2020/9/27
 * Author: songjialin
 */
public interface OkHttpClientLoader {
    OkHttpClient getBaseHttpClient();
}
