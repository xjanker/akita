/*
 * Copyright 2011 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package org.akita.proxy;


/**
 * Dynamic Proxy Factory, to get the rawapi's proxy
 * @author zhe.yangz 2011-12-28 下午02:57:33
 */
public class ProxyFactory {

    /**
     * @deprecated use Akita.createAPI() to instead
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public static <T> T getProxy(Class<T> clazz){
        ProxyInvocationHandler proxyHandler = new ProxyInvocationHandler();
        return (T) proxyHandler.bind(clazz);
    }
    
}
