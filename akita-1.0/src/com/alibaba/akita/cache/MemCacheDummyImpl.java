/*
 * Copyright 1999-2101 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.akita.cache;

import com.alibaba.akita.cache.MemCache;

import java.util.HashMap;
import java.util.Map;

/**
 * Dummy Mem Cache Impl
 * Created with IntelliJ IDEA.
 * User: justin
 * Date: 12-4-25
 * Time: 下午9:19
 *
 * @author Justin Yang
 */
public class MemCacheDummyImpl<K, V> implements MemCache<K, V> {
    protected MemCacheDummyImpl(int maxSize) {
    }

    @Override
    public V get(K key) {
        return null;  // defaults
    }

    @Override
    public V put(K key, V value) {
        return null;  // defaults
    }

    @Override
    public V remove(K key) {
        return null;  // defaults
    }

    @Override
    public void clear() {
        // defaults
    }

    @Override
    public Map<K, V> snapshot() {
        return new HashMap<K, V>();  // defaults
    }
}
