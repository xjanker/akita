/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.akita.util;

import java.math.BigInteger;
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
}
