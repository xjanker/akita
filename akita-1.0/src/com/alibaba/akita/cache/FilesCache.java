/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.akita.cache;


/**
 * Key为String 
 * V对应文件夹系统中的特定File  
 * in SD卡目录中
 * @author zhe.yangz 2012-3-30 下午03:23:19
 */
public interface FilesCache<V> {
    
    public V get(String key);

    /**
     * @param key
     * @param value if value is null, no effect and return null
     * @return oldValue or null if has no oldValue
     */
    public V put(String key, V value);
    public V remove(String key);

    /**
     * 根据建立Impl对象时所定义的容量受控大小来逐出一批过期内容。
     * @return
     */
    public void evict();

    public void setCacheSize(int cacheSizeInMB);
    
}
