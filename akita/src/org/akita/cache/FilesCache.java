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
package org.akita.cache;


/**
 * Key is String
 * V is specified File of file system on sd card
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
     * evict cache data according to the cache size set.
     * @return
     */
    public void evict();

    /**
     * Clear all this cache data
     * Note: this method maybe Time-consuming, you may run it async
     */
    public void clearCache();

    /**
     * get current cache size in MB
     * @return
     */
    public double getCacheCurrentSizeMB();

    /**
     * set normal cache size limit in MB
     * @param cacheSizeInMB
     */
    public void setCacheSize(int cacheSizeInMB);
    
}
