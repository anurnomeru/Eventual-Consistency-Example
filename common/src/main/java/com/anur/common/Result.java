package com.anur.common;


import lombok.Builder;
import lombok.Data;

/**
 * Created by Anur IjuoKaruKas on 2018/5/7
 * 统一API响应结果封装
 */
@Data
@Builder
public class Result<T> {
    private int code;
    private String message;
    private T data;
}
