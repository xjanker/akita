/*
 * Copyright 1999-2101 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.akita.cache;

import android.graphics.Bitmap;
import android.nfc.Tag;
import com.alibaba.akita.cache.MemCache;
import com.alibaba.akita.util.Log;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 一个MemCacheSoftRefImpl实例中存在一个基于softRef的Cache
 * @author zhe.yangz 2012-3-30 下午03:23:19
 */
public class MemCacheSoftRefImpl<K, V> implements MemCache<K, V> {
    private static final String TAG = "MemCacheSoftRefImpl<K, V>";

    // Soft cache for such as bitmaps
    private static final int HARD_CACHE_CAPACITY = 20;
    private final ConcurrentHashMap<K, SoftReference<V>> mSoftVauleCache =
            new ConcurrentHashMap<K, SoftReference<V>>(HARD_CACHE_CAPACITY);

    /**
     * 已经构造出一个HARD_CACHE_CAPACITY的
     * SoftRef Cache.
     */
    protected MemCacheSoftRefImpl(){
    }
    
    public V get(K key){
        // Then try the soft reference cache
        SoftReference<V> valueReference = mSoftVauleCache.get(key);
        if (valueReference != null) {
            final V bitmap = valueReference.get();
            if (bitmap instanceof Bitmap) { // for Bitmap of V
                if (bitmap != null && !((Bitmap)bitmap).isRecycled()) {
                    // V found in soft cache
                    return bitmap;
                } else {
                    // Soft reference has been GCed
                    mSoftVauleCache.remove(key);
                    return null;
                }
            } else {
                if (bitmap != null) {
                    // V found in soft cache
                    return bitmap;
                } else {
                    // Soft reference has been GCed
                    mSoftVauleCache.remove(key);
                    return null;
                }
            }
        }
        return null;
    }

    /**
     *
     * @param key
     * @param value
     * @return if null, may be no old value, or old value has been GCed.
     */
    public V put(K key, V value) {
        if (key != null && value != null) {
            SoftReference<V> sr = mSoftVauleCache.put(key, new SoftReference<V>(value));
            if (sr != null) {
                return sr.get();
            } else {
                return  null;
            }
        } else {
            return null;
        }
    }

    /**
     *
     * @param key
     * @return if null, may be has no such key, or key's value has been GCed.
     */
    public V remove(K key) {
        SoftReference<V> sr = mSoftVauleCache.remove(key);
        if (sr != null) {
            return sr.get();
        } else {
            return  null;
        }
    }

    @Override
    public void clear() {
        mSoftVauleCache.clear();
    }

    /**
     * no need to implement, so return new map now.
     * @return
     */
    @Override
    public Map<K, V> snapshot() {
        return new LinkedHashMap<K, V>();
    }
}
