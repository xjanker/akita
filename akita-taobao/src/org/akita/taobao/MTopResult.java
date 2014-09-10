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
package org.akita.taobao;

import org.akita.exception.AkInvokeException;
import org.akita.util.JsonMapper;
import org.akita.util.Log;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: justinyang
 * Date: 13-3-12
 * Time: PM5:00
 */
public class MTopResult<T> {
    private static final String TAG = "MTopResult<T>";

    private T _data;
    public T getData() {
        return _data;
    }

    public String api;
    public String v;
    public ArrayList<String> ret;

    public JsonNode data;

    @SuppressWarnings("unchecked")
    public T getData(Class<T> entityClass) throws AkInvokeException {
        if (data != null && data.size() > 0) {

            try {
                if (String.class.equals(entityClass)) {
                    _data = (T)JsonMapper.node2json(data);
                } else {
                    T t = JsonMapper.node2pojo(data, entityClass);
                    try {
                        Method method = entityClass.getDeclaredMethod("fulFill");
                        method.setAccessible(true);
                        t = (T) method.invoke(t);
                    } catch (SecurityException e) {
                        Log.v(TAG, "Security in fulFill");
                    } catch (NoSuchMethodException e) {
                        Log.v(TAG, "NoSuchMethod of fulFill.");
                    }
                    _data = t;
                }
                return _data;
            } catch (JsonProcessingException e) {
                Log.e(TAG, data.toString());  // log can print the error return-string
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
