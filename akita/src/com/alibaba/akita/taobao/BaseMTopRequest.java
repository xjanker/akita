package com.alibaba.akita.taobao;

/**
 * Created with IntelliJ IDEA.
 * User: justinyang
 * Date: 13-3-28
 * Time: AM10:53
 */
public abstract class BaseMTopRequest implements MTopRequest {
    private long clientT = 0;

    public BaseMTopRequest() {
        clientT = System.currentTimeMillis();
    }

    @Override
    public long getClientT() {
        return clientT;  //default
    }
}
