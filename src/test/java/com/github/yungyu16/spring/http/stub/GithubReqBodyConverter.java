package com.github.yungyu16.spring.http.stub;

import com.alibaba.fastjson.JSON;
import com.github.yungyu16.spring.http.converter.ReqBodyConverter;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2020/9/26.
 */
@Component
public class GithubReqBodyConverter implements ReqBodyConverter {
    @Override
    public RequestBody toRequestBody(@NotNull Object entity, Type type) {
        return RequestBody.create(JSON.toJSONBytes(entity), CONTENT_TYPE_JSON);
    }
}
