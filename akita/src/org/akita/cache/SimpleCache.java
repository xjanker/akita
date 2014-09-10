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


import java.util.ArrayList;

/**
 * K\V String 
 * @author zhe.yangz 2012-3-30 下午03:23:19
 */
public interface SimpleCache {

    public String get(String key);

    /**
     * get Latest items, max to num
     * @param num
     * @return
     */
    public ArrayList<String> getLatest(int num);
    public String put(String key, String value);
    public String remove(String key);
    public void removeAll();
    /**
     * Close the db and sth. else.
     * @return
     */
    public void close();
    
}
