package com.github.yungyu16.spring.http.interceptor;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import retrofit2.Invocation;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public abstract class BaseMethodAnnotationInterceptor<T extends Annotation> implements Interceptor {
    private final Class<T> annotationClass;

    public BaseMethodAnnotationInterceptor(Class<T> annotationClass) {
        if (annotationClass == null) {
            throw new NullPointerException("annotationClass");
        }
        this.annotationClass = annotationClass;
    }

    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        Invocation tag = request.tag(Invocation.class);
        if (tag == null) {
            return chain.proceed(request);
        }
        Method method = tag.method();
        T annotation = method.getAnnotation(annotationClass);
        if (annotation == null) {
            return chain.proceed(request);
        }
        return doIntercept(method, annotation, chain, request);
    }

    protected abstract Response doIntercept(@NotNull Method method, @NotNull T annotation, @NotNull Chain chain, @NotNull Request request) throws IOException;
}
