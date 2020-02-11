package com.zzq.util;

public class Result {

    private int code;
    private String msg;
    private Object data;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }

    private Result(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static Result scuess(Object data) {
        return new Result(200, "成功！", data);
    }

    public static Result scuess(String msg, Object data) {
        return new Result(200, msg, data);
    }

    public static Result scuess(int code, String msg, Object data) {
        return new Result(code, msg, data);
    }

    public static Result fail(String msg) {
        return new Result(500, msg);
    }

    public static Result fail(int code, String msg) {
        return new Result(code, msg);
    }

}
