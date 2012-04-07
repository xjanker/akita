/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.akita.cache;


/**
 * K\V String 
 * @author zhe.yangz 2012-3-30 下午03:23:19
 */
public interface SimpleCache {

    public String get(String key);
    public String put(String key, String value);
    public String remove(String key);
    
}
