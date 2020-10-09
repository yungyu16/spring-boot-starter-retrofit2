package com.github.yungyu16.spring.retrofit.converter;

import com.github.yungyu16.spring.retrofit.error.RetrofitException;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2020/9/26.
 */
public class DefaultResponseConverter implements ResponseConverter {

    @Override
    public Object fromResponseBody(@NotNull ResponseBody body, Type type) throws IOException {
        try {
            return body.source().readString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RetrofitException("网络错误", e);
        }
    }
}
