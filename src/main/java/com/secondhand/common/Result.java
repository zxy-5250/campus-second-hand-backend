package com.secondhand.common;

import lombok.Data;

/**
 * 统一返回结果封装
 */
@Data
public class Result<T> {
    private Integer code;   // 200=成功，500=失败，401=未登录/无权限
    private String msg;
    private T data;

    public Result() {
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> ok() {
        return new Result<>(200, "操作成功", null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "操作成功", data);
    }

    public static <T> Result<T> ok(String msg, T data) {
        return new Result<>(200, msg, data);
    }

    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    /** 仅带提示消息的成功结果（返回 Result<Void>，专用于无需返回数据、仅带提示的场景） */
    public static Result<Void> ok(String msg) {
        return new Result<>(200, msg, null);
    }

}
