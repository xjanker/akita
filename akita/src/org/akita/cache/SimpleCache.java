/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package org.akita.cache;


import java.util.ArrayList;

/**
 * K\V String 
 * @author zhe.yangz 2012-3-30 下午03:23:19
 */
public interface SimpleCache {

    public String get(String key);

    /**
     * get Latest items, max to num
     * @param num
     * @return
     */
    public ArrayList<String> getLatest(int num);
    public String put(String key, String value);
    public String remove(String key);
    public void removeAll();
    /**
     * Close the db and sth. else.
     * @return
     */
    public void close();
    
}
