package com.wenyan.common.result;

import lombok.Data;

import java.io.Serializable;

/** 统一返回结构 */
@Data
public class Result<T> implements Serializable {
    private Integer code;
    private String message;
    private T data;

    public Result() {}

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }
    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }
}
