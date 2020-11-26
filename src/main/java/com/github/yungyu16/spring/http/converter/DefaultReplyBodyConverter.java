package com.github.yungyu16.spring.http.converter;

import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2020/9/26.
 */
public class DefaultReplyBodyConverter implements ReplyBodyConverter {

    @Override
    public Object fromResponseBody(@NotNull ResponseBody body, Type type) throws IOException {
        return body.source().readString(StandardCharsets.UTF_8);
    }
}
