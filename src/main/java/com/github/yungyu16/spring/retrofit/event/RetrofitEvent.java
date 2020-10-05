package com.github.yungyu16.spring.retrofit.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2020/9/25.
 */
public class RetrofitEvent extends ApplicationEvent {
    public RetrofitEvent(String source) {
        super(source);
    }
}
