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
package org.akita.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *  
 * @author zhe.yangz 2012-3-31 下午02:44:07
 */
public class HashUtil {

    /**
     * 
     * @param ori
     * @return 32 length String; if fails, then return ori String. 
     */
    public static String md5(String ori) {
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            md.update(ori.getBytes(), 0, ori.length());
            byte[] bytes = md.digest();
            BigInteger i = new BigInteger(1, bytes);
            return String.format("%1$032x", i);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return ori;
        }
    }

    public static String sha1(String ori) {
        try {
            MessageDigest md = MessageDigest.getInstance("sha1");
            md.update(ori.getBytes(), 0, ori.length());
            BigInteger i = new BigInteger(1, md.digest());
            return String.format("%1$040x", i);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return ori;
        }
    }

    public static final String HMAC_SHA1 = "HmacSHA1";
    public static byte[] hmacSha1(String[] datas, byte[] key) {
        SecretKeySpec signingKey = new SecretKeySpec(key, HMAC_SHA1);
        Mac mac = null;
        try {
            mac = Mac.getInstance(HMAC_SHA1);
            mac.init(signingKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        try {
            for (String data : datas) {
                mac.update(data.getBytes(StringUtil.CHARSET_NAME_UTF8));
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return mac.doFinal();
    }

    public static final String HMAC_MD5 = "HmacMD5";
    public static byte[] hmacMd5(String[] datas, byte[] key) {
        SecretKeySpec signingKey = new SecretKeySpec(key, HMAC_MD5);
        Mac mac = null;
        try {
            mac = Mac.getInstance(HMAC_MD5);
            mac.init(signingKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        try {
            for (String data : datas) {
                mac.update(data.getBytes(StringUtil.CHARSET_NAME_UTF8));
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return mac.doFinal();
    }


}
