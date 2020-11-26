package com.github.yungyu16.spring.http.stub;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TestInterceptor1 implements Interceptor {
    public Response intercept(@NotNull Chain chain) throws IOException {
        System.out.println("===============================");
        System.out.println("===============================");
        System.out.println("=============TestInterceptor1==============");
        System.out.println("===============================");
        System.out.println("===============================");
        System.out.println("===============================");
        Request request = chain.request();
        return chain.proceed(request);
    }
}
