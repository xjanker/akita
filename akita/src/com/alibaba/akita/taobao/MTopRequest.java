package com.alibaba.akita.taobao;

/**
 * Created with IntelliJ IDEA.
 * User: justinyang
 * Date: 13-3-14
 * Time: PM2:29
 */
public interface MTopRequest {
    /**
     * not null
     * @return
     */
    public String getApi();

    /**
     * v
     * @return not null, or ”1.0“、”2.0“... or "*"
     */
    public String getV();

    /**
     * 返回客户端时间TimeMillis
     * @return
     */
    public long getClientT();
}
