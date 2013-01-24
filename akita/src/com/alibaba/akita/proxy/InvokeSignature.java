/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.akita.proxy;

import java.util.ArrayList;

import com.alibaba.akita.annotation.AkSignature;
import org.apache.http.NameValuePair;


/**
 * interface of the signature class 
 * @author zhe.yangz 2012-2-17 下午08:18:10
 */
public interface InvokeSignature {
    public String getSignatureParamName();
    public String signature(AkSignature akSig,  String invokeUrl, ArrayList<NameValuePair> params);
}
