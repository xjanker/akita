/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.akita.cache;


import java.util.Map;

/**
 * Mem level Cache
 * @author zhe.yangz 2012-3-30 下午03:23:19
 */
public interface MemCache<K, V> {

    public V get(K key);
    public V put(K key, V value);
    public V remove(K key);

    /**
     * Clear all kvs of this MemCache
     */
    public void clear();

    /**
     * Get a snapshot of cache
     */
    public Map<K, V> snapshot();
    
}
