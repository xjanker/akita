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
