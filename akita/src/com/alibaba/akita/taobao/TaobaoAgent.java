package com.alibaba.akita.taobao;

import com.alibaba.akita.Akita;
import com.alibaba.akita.exception.AkInvokeException;
import com.alibaba.akita.exception.AkServerStatusException;
import com.alibaba.akita.util.DateUtil;

import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: justinyang
 * Date: 13-2-17
 * Time: PM3:55
 */
public class TaobaoAgent {

    private String app_key = null;
    private String app_secret = null;
    private String partner_id = null;

    private TaobaoAgent() {

    }

    public static TaobaoAgent createAgent(String appKey, String appSecret) {
        TaobaoAgent taobaoAgent = new TaobaoAgent();
        taobaoAgent.app_key = appKey;
        taobaoAgent.app_secret = appSecret;
        taobaoAgent.partner_id = "top-apitools";
        return taobaoAgent;
    }

    public String topAPI(String method, String session, Map<String,String> appLayerData)
            throws AkInvokeException, AkServerStatusException {
        TopAPI topAPI = Akita.createAPI(TopAPI.class);
        return topAPI.execute(DateUtil.getTimestampDatetime(System.currentTimeMillis()),"2.0", app_key, app_secret,
                method, session, partner_id,"json", "hmac", appLayerData);
    }
}
