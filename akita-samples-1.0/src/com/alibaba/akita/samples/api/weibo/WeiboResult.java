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
package com.alibaba.akita.samples.api.weibo;

import com.alibaba.akita.exception.AkInvokeException;
import com.alibaba.akita.util.JsonMapper;
import com.alibaba.akita.util.Log;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Ocean prama2 format returned json's object 
 * @author zhe.yangz 2012-2-1 下午05:00:24
 */
 public class WeiboResult<T> {
    private static final String TAG = "WeiboResult<T>";
    
    public Head head;
    public static class Head {
        public String message;
        public String code;
    }

    public JsonNode body;
    
    @SuppressWarnings("unchecked")
    public T getBody(Class<T> entityClass) throws AkInvokeException {
        if(body != null && body.size()>0){
            
            try {
                T t = JsonMapper.node2pojo(body, entityClass);
                try {
                    Method method = entityClass.getDeclaredMethod("fulFill");
                    method.setAccessible(true);
                    t = (T) method.invoke(t);
                } catch (SecurityException e) {
                    Log.v(TAG, "security in fulFill");
                } catch (NoSuchMethodException e) {
                    Log.v(TAG, "NoSuchMethod of fulFill.");
                }
                return t;
            } catch (JsonProcessingException e) {
                Log.e(TAG, body.toString());  // log can print the error return-string
                throw new AkInvokeException(AkInvokeException.CODE_JSONPROCESS_EXCEPTION,
                        e.getMessage(), e);
            } catch (IOException e) {
                throw new AkInvokeException(AkInvokeException.CODE_IO_EXCEPTION,
                        e.getMessage(), e);
            } catch (InvocationTargetException ite) {
                throw new AkInvokeException(AkInvokeException.CODE_FULFILL_INVOKE_EXCEPTION,
                        ite.getMessage(), ite);
            } catch (IllegalAccessException e) {
                throw new AkInvokeException(AkInvokeException.CODE_FULFILL_INVOKE_EXCEPTION,
                        e.getMessage(), e);
            }
        }
        return null;
    }
}
