package org.akita.taobao;

/**
 * Created with IntelliJ IDEA.
 * User: justinyang
 * Date: 13-3-14
 * Time: PM2:29
 */
public interface TopRequest {
    /**
     * not null
     * @return
     */
    public String getMethod();

    /**
     * v
     * @return not null, or ”1.0“、”2.0“... or "*"
     */
    public String getV();

    /**
     * client t
     * @return
     */
    public long getT();
}
