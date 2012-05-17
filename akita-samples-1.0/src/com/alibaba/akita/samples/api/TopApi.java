/*
 * Copyright 1999-2101 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.akita.samples.api;

import com.alibaba.akita.annotation.AkAPI;
import com.alibaba.akita.annotation.AkGET;
import com.alibaba.akita.annotation.AkParam;
import com.alibaba.akita.exception.AkInvokeException;
import com.alibaba.akita.exception.AkServerStatusException;
import com.alibaba.akita.samples.api.top.ItemGetResult;

/**
 * Created with IntelliJ IDEA.
 * User: justin
 * Date: 12-4-4
 * Time: 上午12:36
 *
 * @author Justin Yang
 */
public interface TopApi {
    @AkGET
    @AkAPI(url="http://gw.api.taobao.com/router/rest")
    ItemGetResult taobao_item_get(
            @AkParam("sign") String sign,
            @AkParam("timestamp") String timestamp,
            @AkParam("v") String v,
            @AkParam("app_key") int app_key,
            @AkParam("method") String method,
            @AkParam("partner_id") String partner_id,
            @AkParam("format") String format,
            @AkParam("num_iid") long num_iid,
            @AkParam("fields") String fields
    ) throws AkInvokeException, AkServerStatusException;
}
