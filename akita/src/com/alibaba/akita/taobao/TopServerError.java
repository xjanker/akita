package com.alibaba.akita.taobao;

/**
 * Created with IntelliJ IDEA.
 * User: justinyang
 * Date: 13-5-7
 * Time: AM9:47
 */
public class TopServerError {
    public ResponseError error_response;
    public static class ResponseError {
        public int code;
        public String msg;
    }
}
