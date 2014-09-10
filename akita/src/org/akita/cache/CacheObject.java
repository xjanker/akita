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
 * 将要持久的对象做一个简单的包装，增加一个持久化时间，方便对Cache中的内容进行排序或者LRU算法刷新等。时间新的缓存对象大于时间旧的缓存对象。
 * 
 * @author Pancras Chow
 */
public class CacheObject implements Comparable<CacheObject> {


    /**
     * 对象进入缓存的时间
     */
    public long                cacheTime;
    /**
     * 对象缓存所使用的Key
     */
    public String              key;
    /**
     * 实际缓存的内容
     */
    public String              value;

    protected CacheObject(String key, String value){
        this.key = key;
        this.value = value;
        this.cacheTime = System.currentTimeMillis();
    }

    @Override
    public int compareTo(CacheObject co) {
        if (cacheTime > co.cacheTime) {
            return 1;
        } else if (cacheTime < co.cacheTime) {
            return -1;
        } else {
            return 0;
        }
    }

}
