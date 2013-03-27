package com.alibaba.akita.taobao;

import com.alibaba.akita.Akita;
import com.alibaba.akita.exception.AkInvokeException;
import com.alibaba.akita.exception.AkServerStatusException;
import com.alibaba.akita.util.DateUtil;
import com.alibaba.akita.util.JsonMapper;
import com.alibaba.akita.util.Log;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.type.JavaType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: justinyang
 * Date: 13-2-17
 * Time: PM3:55
 */
public class TaobaoAgent {
    private static final String TAG = "TaobaoAgent";

    private static TaobaoAgent sCacheTaobaoAgent = null;

    private MTopAPI mTopAPI = null;
    private TopAPI topAPI = null;

    private String app_key = null;
    private String app_secret = null;
    private String partner_id = null;
    private long timeMillisDiff = 0;

    private TaobaoAgent() {

    }

    public static TaobaoAgent createAgent(String appKey, String appSecret) {
        if (sCacheTaobaoAgent != null) {
            sCacheTaobaoAgent.app_key = appKey;
            sCacheTaobaoAgent.app_secret = appSecret;
            sCacheTaobaoAgent.partner_id = "top-apitools";
            return sCacheTaobaoAgent;
        } else {
            TaobaoAgent taobaoAgent = new TaobaoAgent();
            taobaoAgent.app_key = appKey;
            taobaoAgent.app_secret = appSecret;
            taobaoAgent.partner_id = "top-apitools";
            return taobaoAgent;
        }
    }

    /**
     * 设置服务器与客户端之间的时间差
     * timeMillisDiff = 服务器时间 - 客户端时间
     * @param timeMillisDiff
     */
    public void setTimeDiff(long timeMillisDiff) {
        this.timeMillisDiff = timeMillisDiff;
    }

    /* ========
    TOP part
    ======== */
    public <T> T topAPI(TopRequest topRequest, Class<T> clazz)
            throws AkInvokeException, AkServerStatusException {
        return topAPI(null, topRequest, clazz);
    }
    public <T> T topAPI(String session, TopRequest topRequest, Class<T> clazz)
            throws AkInvokeException, AkServerStatusException {
        if (topAPI == null) {
            topAPI = Akita.createAPI(TopAPI.class);
        }
        Map<?,?> appLayerDataTemp = null;
        Map<String, String> appLayerData = new HashMap<String, String>();
        try {
            appLayerDataTemp = JsonMapper.json2map(JsonMapper.pojo2json(topRequest));
            Iterator<? extends Map.Entry<?,?>> iter =  appLayerDataTemp.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<?,?> entry = iter.next();
                String key = String.valueOf( entry.getKey() );
                String value = String.valueOf( entry.getValue() );
                appLayerData.put(key, value);
            }
        } catch (IOException e) {
            throw new AkInvokeException(AkInvokeException.CODE_FILE_NOT_FOUND,
                    e.getMessage(), e);        }
        String retStr =
                topAPI.online(DateUtil.getTimestampDatetime(System.currentTimeMillis()+timeMillisDiff),
                        topRequest.getV(),
                        app_key, app_secret,
                        topRequest.getMethod(), session, partner_id, "json", "hmac", appLayerData);

        try {
            if (String.class.equals(clazz)) {
                return (T)retStr;
            } else {
                return JsonMapper.json2pojo(retStr, clazz);
            }
        } catch (JsonProcessingException e) {
            Log.e(TAG, retStr, e);  // log can print the error return-string
            throw new AkInvokeException(AkInvokeException.CODE_JSONPROCESS_EXCEPTION,
                    e.getMessage(), e);
        } catch (IOException e) {
            throw new AkInvokeException(AkInvokeException.CODE_IO_EXCEPTION,
                    e.getMessage(), e);
        }

    }

    /* ========
    MTOP part
    ======== */
    public <T> MTopResult<T> mtopAPI(MTopRequest request, Class<T> clazz)
            throws AkInvokeException, AkServerStatusException {
        return mtopAPI(null, null, request, clazz);
    }
    public <T> MTopResult<T> mtopAPI(String ecode, String ext, MTopRequest request, Class<T> clazz)
            throws AkInvokeException, AkServerStatusException {
        if (mTopAPI == null) {
            mTopAPI = Akita.createAPI(MTopAPI.class);
        }
        String dataStr = "{}";
        try {
            dataStr = JsonMapper.pojo2json(request);
        } catch (IOException e) {
            Log.e(TAG, e.toString(), e);
        }
        String retStr = mTopAPI.online(ecode, app_secret, app_key,
                "1.1.2", request.getApi(),
                request.getV(), "100860@juhuasuan_android_1.1.2",
                "460011610649537", "352110052381283",
                (System.currentTimeMillis()+timeMillisDiff)/1000,
                dataStr, ext, null, "md5");

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