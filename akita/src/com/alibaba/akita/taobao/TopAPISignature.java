package com.alibaba.akita.taobao;

import com.alibaba.akita.annotation.AkSignature;
import com.alibaba.akita.proxy.InvokeSignature;
import com.alibaba.akita.util.HashUtil;
import com.alibaba.akita.util.StringUtil;
import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
