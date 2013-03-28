package com.alibaba.akita.taobao;

/**
 * Created with IntelliJ IDEA.
 * User: justinyang
 * Date: 13-3-28
 * Time: AM10:59
 */
public abstract class BaseTopRequest implements TopRequest {
    private long clientT = 0;

    public BaseTopRequest() {
        clientT = System.currentTimeMillis();
    }

    @Override
    public long getClientT() {
        return clientT;  //default
    }
}
