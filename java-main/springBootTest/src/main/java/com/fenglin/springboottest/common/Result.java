package com.fenglin.springboottest.common;

/**
 * 统一返回结果
 */
public class Result<T> {

    private Integer code;   // 200 成功，400 失败
    private String msg;
    private T data;

    public Result() {
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, data);
    }

    public static <T> Result<T> success(String msg) {
        return new Result<>(200, msg, null);
    }

    public static <T> Result<T> fail(String msg) {
        return new Result<>(400, msg, null);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
