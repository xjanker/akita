package com.alibaba.akita.taobao;

import com.alibaba.akita.annotation.AkAPI;
import com.alibaba.akita.annotation.AkGET;
import com.alibaba.akita.annotation.AkParam;
import com.alibaba.akita.annotation.AkSignature;
import com.alibaba.akita.exception.AkInvokeException;
import com.alibaba.akita.exception.AkServerStatusException;

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
    @AkGET
    @AkSignature(using = TopAPISignature.class)
    @AkAPI(url="http://gw.api.taobao.com/router/rest")
    String top_online(
            @AkParam(value = "timestamp", encode = "utf8") String timestamp,
            @AkParam("v") String v,
            @AkParam("app_key") String app_key,
            @AkParam("app_secret") String app_secret,
            @AkParam("method") String method,
            @AkParam("session") String session,
            @AkParam("partner_id") String partner_id,
            @AkParam("format") String format,
            @AkParam("sign_method") String sign_method,
            @AkParam("$paramMap") Map<String, String> appLayerData
    ) throws AkInvokeException, AkServerStatusException;
}
