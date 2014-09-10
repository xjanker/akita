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
import org.akita.exception.AkServerStatusException;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: justinyang
 * Date: 13-2-17
 * Time: PM3:32
 *
 * eg.
 * http://gw.api.taobao.com/router/rest?sign=719E9C5DCF2E1423702EC66822DE22E4
 * &timestamp=2013-02-17+15%3A44%3A42
 * &v=2.0
 * &app_key=12129701
 * &method=taobao.item.get
 * &partner_id=top-apitools
 * &format=json
 * &num_iid=16788418057
 * &fields=detail_url,num_iid,title
 *
 */
public interface TopAPI {

//    @AkGET
//    @AkSignature(using = TopAPISignature.class)
//    @AkAPI(url="http://gw.api.taobao.com/router/rest")
//    String top_online(
//            @AkParam(value = "timestamp", encode = "utf8") String timestamp,
//            @AkParam("v") String v,
//            @AkParam("app_key") String app_key,
//            @AkParam("app_secret") String app_secret,
//            @AkParam("method") String method,
//            @AkParam("session") String session,
//            @AkParam("partner_id") String partner_id,
//            @AkParam("format") String format,
//            @AkParam("sign_method") String sign_method,
//            @AkParam(value = "$paramMap", encode = "utf8") Map<String, String> appLayerData
//    ) throws AkInvokeException, AkServerStatusException;


    @AkPOST
    @AkSignature(using = TopAPISignature.class)
    @AkAPI(url="http://gw.api.taobao.com/router/rest")
    String top_online(
            @AkParam(value = "timestamp") String timestamp,
            @AkParam("v") String v,
            @AkParam("app_key") String app_key,
            @AkParam("app_secret") String app_secret,
            @AkParam("method") String method,
            @AkParam("session") String session,
            @AkParam("partner_id") String partner_id,
            @AkParam("format") String format,
            @AkParam("sign_method") String sign_method,
            @AkParam(value = "$paramMap") Map<String, String> appLayerData
    ) throws AkInvokeException, AkServerStatusException;

    @AkPOST
    @AkSignature(using = TopAPISignature.class)
    @AkAPI(url="http://110.75.14.63/top/router/rest")
    String top_predeploy(
            @AkParam(value = "timestamp") String timestamp,
            @AkParam("v") String v,
            @AkParam("app_key") String app_key,
            @AkParam("app_secret") String app_secret,
            @AkParam("method") String method,
            @AkParam("session") String session,
            @AkParam("partner_id") String partner_id,
            @AkParam("format") String format,
            @AkParam("sign_method") String sign_method,
            @AkParam(value = "$paramMap") Map<String, String> appLayerData
    ) throws AkInvokeException, AkServerStatusException;

    @AkPOST
    @AkSignature(using = TopAPISignature.class)
    @AkAPI(url="http://api.daily.taobao.net/router/rest")
    String top_daily(
            @AkParam(value = "timestamp") String timestamp,
            @AkParam("v") String v,
            @AkParam("app_key") String app_key,
            @AkParam("app_secret") String app_secret,
            @AkParam("method") String method,
            @AkParam("session") String session,
            @AkParam("partner_id") String partner_id,
            @AkParam("format") String format,
            @AkParam("sign_method") String sign_method,
            @AkParam(value = "$paramMap") Map<String, String> appLayerData
    ) throws AkInvokeException, AkServerStatusException;

}
