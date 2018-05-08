package com.anur.common;

/**
 * Created by Anur IjuoKaruKas on 2018/5/7
 * 响应结果生成工具
 */
public class ResultGenerator {

    public static Result genSuccessResult() {
        return Result.builder().code(ResultCode.SUCCESS.code).build();
    }

    public static Result genSuccessResult(Object data) {
        return Result.builder().code(ResultCode.SUCCESS.code).data(data).build();
    }

    public static Result genFailResult(String message) {
        return Result.builder().code(ResultCode.FAIL.code).message(message).build();
    }

    public static Result genExceptionResult(Exception e) {
        return Result.builder().code(ResultCode.FAIL.code).message(e.getMessage()).build();
    }
}
