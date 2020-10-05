package com.github.yungyu16.spring.retrofit.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2020/9/25.
 */
public class RequestStartEvent extends ApplicationEvent {
    public RequestStartEvent(String source) {
        super(source);
    }
}
