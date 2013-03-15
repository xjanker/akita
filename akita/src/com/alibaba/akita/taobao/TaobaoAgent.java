package com.alibaba.akita.taobao;

import com.alibaba.akita.Akita;
import com.alibaba.akita.exception.AkInvokeException;
import com.alibaba.akita.exception.AkServerStatusException;
import com.alibaba.akita.util.DateUtil;
import com.alibaba.akita.util.JsonMapper;
import com.alibaba.akita.util.Log;
import org.codehaus.jackson.JsonProcessingException;

import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: justinyang
 * Date: 13-2-17
 * Time: PM3:55
 */
public class TaobaoAgent {
    private static final String TAG = "TaobaoAgent";

    private static TaobaoAgent m_cacheTaobaoAgent = null;

    private String app_key = null;
    private String app_secret = null;
    private String partner_id = null;

    private TaobaoAgent() {

    }

    public static TaobaoAgent createAgent(String appKey, String appSecret) {
        if (m_cacheTaobaoAgent != null) {
            m_cacheTaobaoAgent.app_key = appKey;
            m_cacheTaobaoAgent.app_secret = appSecret;
            m_cacheTaobaoAgent.partner_id = "top-apitools";
            return m_cacheTaobaoAgent;
        } else {
            TaobaoAgent taobaoAgent = new TaobaoAgent();
            taobaoAgent.app_key = appKey;
            taobaoAgent.app_secret = appSecret;
            taobaoAgent.partner_id = "top-apitools";
            return taobaoAgent;
        }
    }

    public String topAPI(String method, String session, Map<String,String> appLayerData)
            throws AkInvokeException, AkServerStatusException {
        TopAPI topAPI = Akita.createAPI(TopAPI.class);
        return topAPI.execute(DateUtil.getTimestampDatetime(System.currentTimeMillis()),"2.0", app_key, app_secret,
                method, session, partner_id,"json", "hmac", appLayerData);
    }

    public <T> MTopResult<T> mtopAPI(MTopRequest request, Class<T> clazz)
            throws AkInvokeException, AkServerStatusException {
        MTopAPI mTopAPI = Akita.createAPI(MTopAPI.class);
        String dataStr = "{}";
        try {
            dataStr = JsonMapper.pojo2json(request);
        } catch (IOException e) {
            Log.e(TAG, e.toString(), e);
        }
        String retStr = mTopAPI.online(null, app_secret, app_key, "1.1.1", request.getApi(),
                "1.0", "100860@juhuasuan_android_1.1.1",
                "460011610649537", "352110052381283",
                1363068821/*System.currentTimeMillis()/1000*/,
                dataStr, null, "md5");

        try {
            MTopResult mTopResult = JsonMapper.json2pojo(retStr, MTopResult.class);
            mTopResult.getData(clazz);
            return mTopResult;
        } catch (JsonProcessingException e) {
            Log.e(TAG, retStr, e);  // log can print the error return-string
            throw new AkInvokeException(AkInvokeException.CODE_JSONPROCESS_EXCEPTION,
                    e.getMessage(), e);
        } catch (IOException e) {
            throw new AkInvokeException(AkInvokeException.CODE_IO_EXCEPTION,
                    e.getMessage(), e);
        }
    }
}