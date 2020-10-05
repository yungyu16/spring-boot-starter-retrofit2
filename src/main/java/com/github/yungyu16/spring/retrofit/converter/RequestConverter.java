package com.github.yungyu16.spring.retrofit.converter;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.MimeTypeUtils;

import java.lang.reflect.Type;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2020/9/26.
 */
public interface RequestConverter {
    MediaType CONTENT_TYPE_JSON = MediaType.get(MimeTypeUtils.APPLICATION_JSON_VALUE);

    RequestBody toRequestBody(@NotNull Object entity, Type type);

    default RequestBody buildRequestBody(byte[] payload) {
        return RequestBody.create(payload, CONTENT_TYPE_JSON);
    }
}
