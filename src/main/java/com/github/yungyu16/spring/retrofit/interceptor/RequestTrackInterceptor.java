package com.github.yungyu16.spring.retrofit.interceptor;

import com.github.yungyu16.spring.retrofit.annotion.RequestTrack;
import okhttp3.*;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * CreatedDate: 2020/9/14
 * Author: songjialin
 */
@RequestTrack
public class RequestTrackInterceptor extends BaseMethodAnnotationInterceptor<RequestTrack> {
    private ApplicationEventPublisher applicationEventPublisher;

    public RequestTrackInterceptor(ApplicationEventPublisher applicationEventPublisher) {
        super(RequestTrack.class);
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    protected Response doIntercept(@NotNull Method method, @NotNull RequestTrack annotation, @NotNull Chain chain, @NotNull Request request) throws IOException {
        String topic = annotation.topic();
        String[] tags = annotation.tags();
        Map<String, List<String>> headerMap = request.headers().toMultimap();
        String httpMethod = request.method();
        String url = request.url().toString();
        String reqBody = parseJsonBody(request.body());
        IOException err = null;
        Response response = null;
        LocalDateTime start = LocalDateTime.now();
        try {
            response = chain.proceed(request);
            Headers headers = response.headers();
            ResponseBody respBody = response.body();
        } catch (IOException e) {
            err = e;
            throw e;
        } finally {
            LocalDateTime end = LocalDateTime.now();
        }
        return response;
    }

    private String parseJsonBody(RequestBody body) throws IOException {
        if (body == null) {
            return null;
        }
        MediaType mediaType = body.contentType();
        if (mediaType == null) {
            return null;
        }
        Charset charset = mediaType.charset();
        if (mediaType.toString().contains("json")) {
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            return buffer.readString(charset == null ? StandardCharsets.UTF_8 : charset);
        }
        return null;
    }
}

