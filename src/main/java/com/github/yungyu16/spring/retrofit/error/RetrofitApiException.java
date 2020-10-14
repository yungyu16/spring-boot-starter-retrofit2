package com.github.yungyu16.spring.retrofit.error;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2020/9/25.
 */
public class RetrofitApiException extends RetrofitException {
    private final String resultCode;

    public RetrofitApiException(String resultCode, String message, Throwable cause) {
        super(message, cause);
        this.resultCode = resultCode;
    }

    public static RetrofitApiException newInstanceWithMsg(String message) {
        return new RetrofitApiException(null, message, null);
    }

    public static RetrofitApiException newInstanceWithCode(String resultCode) {
        return new RetrofitApiException(resultCode, null, null);
    }

    public static RetrofitApiException newInstanceWithMsg(String message, Throwable cause) {
        return new RetrofitApiException(null, message, cause);
    }

    public static RetrofitApiException newInstanceWithCode(String resultCode, Throwable cause) {
        return new RetrofitApiException(resultCode, null, cause);
    }

    public static RetrofitApiException newInstance(String resultCode, String message) {
        return new RetrofitApiException(resultCode, message, null);
    }

    public static RetrofitApiException newInstance(String resultCode, String message, Throwable cause) {
        return new RetrofitApiException(resultCode, message, cause);
    }

    public String getResultCode() {
        return resultCode;
    }
}
