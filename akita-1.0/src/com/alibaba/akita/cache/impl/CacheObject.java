package com.alibaba.akita.cache.impl;


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

    public CacheObject(String key, String value){
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
