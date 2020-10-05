package com.github.yungyu16.spring.retrofit.event;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2020/9/25.
 */
public class RequestEndEvent extends RetrofitEvent {
    public RequestEndEvent(String source) {
        super(source);
    }
}
