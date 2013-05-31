package com.alibaba.akita.taobao;

import com.alibaba.akita.annotation.AkSignature;
import com.alibaba.akita.proxy.InvokeSignature;
import com.alibaba.akita.util.HashUtil;
import com.alibaba.akita.util.StringUtil;
import org.apache.http.NameValuePair;

import java.net.URLDecoder;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: justinyang
 * Date: 13-2-17
 * Time: PM5:05
 */
public class TopAPISignature implements InvokeSignature {
    @Override
    public String getSignatureParamName() {
        return "sign";
    }

    @Override
    public String signature(AkSignature agSig, String invokeUrl,
                            ArrayList<NameValuePair> params, HashMap<String, String> paramsMapOri) {
        String data0 = agSig.data0();
        NameValuePair app_secret = null;
        for (NameValuePair nameValuePair : params) {
            if ("app_secret".equals(nameValuePair.getName())) {
                app_secret = nameValuePair;
            }
        }
        if (app_secret != null) {
            params.remove(app_secret);
            if ("taobao.juwliserver.schedule.add".equals(invokeUrl)
                    && paramsMapOri!=null && paramsMapOri.size() > 0) {  // Hack only for juhuasuan预下单add接口
                HashMap<String, String> paramsMapOriDecoded = new HashMap<String, String>();
                for (Map.Entry<String, String> entry : paramsMapOri.entrySet()) {
                    try {
                        paramsMapOriDecoded.put(entry.getKey(), URLDecoder.decode(entry.getValue(), "UTF-8"));
                    } catch (Exception e) {
                        paramsMapOriDecoded.put(entry.getKey(), entry.getValue());
                    }
                }
                paramsMapOri = paramsMapOriDecoded;
            }
            return generateSignature(data0, app_secret.getValue(), params, paramsMapOri);
        } else {
            return "no_app_secret_found";
        }
    }

    private String generateSignature(String invokeUrl, String app_secret,
                                     ArrayList<NameValuePair> params, HashMap<String, String> paramsMapOri) {
        List<String> paramValueList = new ArrayList<String>();
        for (NameValuePair nvp : params) {
            paramValueList.add(nvp.getName() + paramsMapOri.get(nvp.getName()));
        }
        StringBuilder sbString = new StringBuilder("");
        Collections.sort(paramValueList);
        for (int i = 0; i < paramValueList.size(); i++) {
            sbString.append(paramValueList.get(i));
        }
        String[] sigData = new String[1];
        sigData[0] = sbString.toString();
        final byte[] signature = HashUtil.hmacMd5(sigData, app_secret.getBytes());
        return StringUtil.encodeHexStr(signature);
    }


}
