/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package org.akita.cache;


import android.support.v4.util.LruCache;

import java.util.Map;


/**
 * Caution: use this impl on support_v4
 * @author zhe.yangz 2012-3-30 下午03:23:19
 */
public class MemCacheLruBitmapImpl<K, V> implements MemCache<K, V> {

    private LruCache<K, V> mCache = null;

    protected MemCacheLruBitmapImpl(int maxMByteSize){
        mCache = new LruCache<K, V>(maxMByteSize) {
            @Override
            protected int sizeOf(K key, V value) {
                return super.sizeOf(key, value);
            }

            @Override
            protected void entryRemoved(boolean evicted, K key, V oldValue, V newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
            }
        };

    }
    
    public V get(K key){
        return mCache.get(key);
    }
    
    public V put(K key, V value) {
        return mCache.put(key, value);
    }
    
    public V remove(K key) {
        return mCache.remove(key);
    }

    @Override
    public void clear() {
        mCache.evictAll();
    }

    public Map<K, V> snapshot() {
        return mCache.snapshot();
    }
}
