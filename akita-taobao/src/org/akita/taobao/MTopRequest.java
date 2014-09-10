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

/**
 * Created with IntelliJ IDEA.
 * User: justinyang
 * Date: 13-3-14
 * Time: PM2:29
 */
public interface MTopRequest {
    /**
     * not null
     * @return
     */
    public String getApi();

    /**
     * v
     * @return not null, or ”1.0“、”2.0“... or "*"
     */
    public String getV();

    /**
     * 返回时间TimeMillis
     * @return
     */
    public long getT();
}
