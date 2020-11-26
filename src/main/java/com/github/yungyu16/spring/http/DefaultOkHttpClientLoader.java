package com.github.yungyu16.spring.http;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;


/**
 * CreatedDate: 2020/9/27
 * Author: songjialin
 */
public class DefaultOkHttpClientLoader implements OkHttpClientLoader {
    private static volatile OkHttpClient HTTP_CLIENT;

    @Override
    public OkHttpClient getBaseHttpClient() {
        if (HTTP_CLIENT == null) {
            synchronized (DefaultOkHttpClientLoader.class) {
                if (HTTP_CLIENT == null) {
                    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(HttpLogger::info);
                    HTTP_CLIENT = new OkHttpClient.Builder()
                            .addInterceptor(interceptor)
                            .connectTimeout(5, TimeUnit.SECONDS)
                            .readTimeout(5, TimeUnit.SECONDS)
                            .writeTimeout(5, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
        return HTTP_CLIENT;
    }
}
