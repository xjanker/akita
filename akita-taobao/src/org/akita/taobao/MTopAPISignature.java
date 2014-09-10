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

import org.akita.annotation.AkSignature;
import org.akita.proxy.InvokeSignature;
import org.akita.util.Log;
import org.apache.http.NameValuePair;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: justinyang
 * Date: 13-2-17
 * Time: PM5:05
 */
public class MTopAPISignature implements InvokeSignature {
    @Override
    public String getSignatureParamName() {
        return "sign";
    }

    @Override
    public String signature(AkSignature agSig, String invokeUrl,
                            ArrayList<NameValuePair> params, HashMap<String, String> paramsMapOri) {
        String data0 = agSig.data0();
        String ecode = null;
        String appSecret = "";
        String appKey = "";
        String api = "";
        String v = "";
        String imsi = "";
        String imei = "";
        String t = "";
        String data = "";
        String sid = "";
        Iterator<NameValuePair> iterator = params.iterator();
        while (iterator.hasNext()) {
            NameValuePair entry = iterator.next();
            if ("ecode".equals(entry.getName())) {
                ecode = paramsMapOri.get(entry.getName());
                iterator.remove();
            } else if ("appSecret".equals(entry.getName())) {
                appSecret = paramsMapOri.get(entry.getName());
                iterator.remove();
            } else if ("appKey".equals(entry.getName())) {
                appKey = paramsMapOri.get(entry.getName());
            } else if ("api".equals(entry.getName())) {
                api = paramsMapOri.get(entry.getName());
            } else if ("v".equals(entry.getName())) {
                v = paramsMapOri.get(entry.getName());
            } else if ("imsi".equals(entry.getName())) {
                imsi = paramsMapOri.get(entry.getName());
            } else if ("imei".equals(entry.getName())) {
                imei = paramsMapOri.get(entry.getName());
            } else if ("t".equals(entry.getName())) {
                t = paramsMapOri.get(entry.getName());
            } else if ("data".equals(entry.getName())) {
                data = paramsMapOri.get(entry.getName());
            } else if ("sid".equals(entry.getName())) {
                sid = paramsMapOri.get(entry.getName());
            }
        }

        // remove
        for (NameValuePair nameValuePair: params) {
            if ("ecode".equals(nameValuePair.getName())) {
                params.remove(nameValuePair);
            } else if ("appSecret".equals(nameValuePair.getName())) {
                params.remove(nameValuePair);
            }
        }

        return getSign(appKey, appSecret, api, v, imei, imsi, data, t, ecode);
    }

    private final String SPLIT_STR = "&";

    public final String getSign(String appKey, String appSecret, String api,
                                String v, String imei, String imsi, String data, String t, String ecode) {
        try {
            String appkeySign = md5ToHex(new ByteArrayInputStream(appKey.getBytes("UTF-8")));
            StringBuffer sb = new StringBuffer();

            if (ecode != null) {
                sb.append(ecode);
                sb.append(SPLIT_STR);
            }
            sb.append(appSecret);
            sb.append(SPLIT_STR);
            sb.append(appkeySign);
            sb.append(SPLIT_STR);
            sb.append(api);
            sb.append(SPLIT_STR);
            sb.append(v);
            sb.append(SPLIT_STR);
            sb.append(imei);
            sb.append(SPLIT_STR);
            sb.append(imsi);
            sb.append(SPLIT_STR);
            if(data == null) {
                data = "";
            }
            String dataSign = md5ToHex(new ByteArrayInputStream(data.getBytes("UTF-8")));
            sb.append(dataSign);
            sb.append(SPLIT_STR);
            sb.append(t);

            return md5ToHex(new ByteArrayInputStream(sb.toString().getBytes("UTF-8")));
        } catch (Exception e) {
            Log.e("SecretUtil", "generate sign fail." + e);
        }
        return null;
    }

    public String md5ToHex(InputStream input) throws IOException {
        return digest(input, "md5");
    }

    private static String digest(InputStream input, String algorithm) throws IOException {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            int STREAM_BUFFER_LENGTH = 1024;
            byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
            int read = input.read(buffer, 0, STREAM_BUFFER_LENGTH);

            while (read > -1) {
                messageDigest.update(buffer, 0, read);
                read = input.read(buffer, 0, STREAM_BUFFER_LENGTH);
            }

            return bytesToHexString(messageDigest.digest());

        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Security exception", e);
        }
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
