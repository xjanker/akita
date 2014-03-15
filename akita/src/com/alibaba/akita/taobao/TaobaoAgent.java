package com.alibaba.akita.taobao;

import com.alibaba.akita.Akita;
import com.alibaba.akita.exception.AkInvokeException;
import com.alibaba.akita.exception.AkServerStatusException;
import com.alibaba.akita.util.DateUtil;
import com.alibaba.akita.util.JsonMapper;
import com.alibaba.akita.util.Log;
import org.codehaus.jackson.JsonProcessingException;

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

    private RunMode runMode = RunMode.PRODUCTION;

    private MTopAPI mTopAPI = null;
    private TopAPI topAPI = null;

    public String app_key = null;
    public String app_secret = null;
    private String ttid = null;
    private String imei = null;
    private String imsi = null;
    private String appVersion = null;
    private String partner_id = null;

    public RunMode getRunMode() {
        return runMode;
    }

    public void setRunMode(RunMode runMode) {
        this.runMode = runMode;
    }

    private TaobaoAgent() {/* no public can call this */}

    public static TaobaoAgent createAgent(String appKey, String appSecret, String ttid) {
        return createAgent(appKey, appSecret, ttid, null, null, null);
    }
    public static TaobaoAgent createAgent(String appKey, String appSecret, String ttid,
                                          String imei, String imsi, String appVersion) {
        if (sCacheTaobaoAgent == null) {
            sCacheTaobaoAgent = new TaobaoAgent();
        }
        sCacheTaobaoAgent.app_key = appKey;
        sCacheTaobaoAgent.app_secret = appSecret;
        sCacheTaobaoAgent.ttid = ttid;
        sCacheTaobaoAgent.partner_id = null;
        sCacheTaobaoAgent.imei = imei;
        sCacheTaobaoAgent.imsi = imsi;
        sCacheTaobaoAgent.appVersion = appVersion;
        if (imei == null) {
            sCacheTaobaoAgent.imei = "D1C91C6EA9E79D50F209D8DCB1359D81";
        }
        if (imsi == null) {
            sCacheTaobaoAgent.imsi = "D1C91C6EA9E79D50F209D8DCB1359D81";
        }
        if (appVersion == null) {
            sCacheTaobaoAgent.appVersion = "2.0.0";
        }

        return sCacheTaobaoAgent;
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
                    e.getMessage(), e);
        }

        String retStr = "";

        switch (runMode) {
            case PRODUCTION:
                retStr = topAPI.top_online(DateUtil.getTimestampDatetime(topRequest.getT()),
                        topRequest.getV(),
                        app_key, app_secret,
                        topRequest.getMethod(), session, partner_id, "json", "hmac", appLayerData);

                break;
            case PREDEPLOY:
                retStr = topAPI.top_predeploy(DateUtil.getTimestampDatetime(topRequest.getT()),
                        topRequest.getV(),
                        app_key, app_secret,
                        topRequest.getMethod(), session, partner_id, "json", "hmac", appLayerData);

                break;
            case DALIY:
                retStr = topAPI.top_daily(DateUtil.getTimestampDatetime(topRequest.getT()),
                        topRequest.getV(),
                        app_key, app_secret,
                        topRequest.getMethod(), session, partner_id, "json", "hmac", appLayerData);
                break;
        }

        // TOP的底层出错信息处理
        if (retStr != null && retStr.contains("{\"error_response\":{\"code\"")) {
            throw new AkServerStatusException(AkServerStatusException.CODE_TOP_ERROR, retStr);
        }

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
        return mtopAPI(null, null, null, request, clazz);
    }
    public <T> MTopResult<T> mtopAPI(String ecode, String ext, String sid, MTopRequest request, Class<T> clazz)
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
        String retStr = "";
        switch (runMode) {
            case PRODUCTION:
                retStr = mTopAPI.mtop_production(ecode, app_secret, app_key,
                        appVersion, request.getApi(),
                        request.getV(), ttid, imsi, imei,
                        (request.getT()) / 1000,
                        dataStr, ext, sid, "md5");
                break;
            case PREDEPLOY:
                retStr = mTopAPI.mtop_predeploy(ecode, app_secret, app_key,
                        appVersion, request.getApi(),
                        request.getV(), ttid, imsi, imei,
                        (request.getT()) / 1000,
                        dataStr, ext, sid, "md5");
                break;
            case DALIY:
                retStr = mTopAPI.mtop_daily(ecode, app_secret, app_key,
                        appVersion, request.getApi(),
                        request.getV(), ttid, imsi, imei,
                        (request.getT()) / 1000,
                        dataStr, ext, sid, "md5");
                break;
        }

        try {
            MTopResult mTopResult = JsonMapper.json2pojo(retStr, MTopResult.class);
            mTopResult.getData(clazz);
            return mTopResult;
        } catch (JsonProcessingException e) {
            Log.e(TAG, retStr, e);  //  log can print the error return-string
            throw new AkInvokeException(AkInvokeException.CODE_JSONPROCESS_EXCEPTION,
                    e.getMessage(), e);
        } catch (IOException e) {
            throw new AkInvokeException(AkInvokeException.CODE_IO_EXCEPTION,
                    e.getMessage(), e);
        }
    }
}