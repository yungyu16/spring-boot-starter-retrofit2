package com.github.yungyu16.spring.retrofit.error;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2020/9/25.
 */
public class RetrofitException extends RuntimeException {
    public RetrofitException() {
        super();
    }

    public RetrofitException(String message) {
        super(message);
    }

    public RetrofitException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetrofitException(Throwable cause) {
        super(cause);
    }

    protected RetrofitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
