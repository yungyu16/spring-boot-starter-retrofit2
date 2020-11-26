package com.github.yungyu16.spring.http.stub;

import com.alibaba.fastjson.JSON;
import com.github.yungyu16.spring.http.converter.ReplyBodyConverter;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2020/9/26.
 */
@Component
public class GithubReplyBodyConverter implements ReplyBodyConverter {

    @Override
    public Object fromResponseBody(@NotNull ResponseBody body, Type type) throws IOException {
        return JSON.parseObject(body.string(), type);
    }
}
