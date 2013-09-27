/*
 * Copyright 2011 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.akita.proxy;

import com.alibaba.akita.annotation.*;
import com.alibaba.akita.exception.AkInvokeException;
import com.alibaba.akita.io.HttpInvoker;
import com.alibaba.akita.util.GsonUtil;
import com.alibaba.akita.util.JsonMapper;
import com.alibaba.akita.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonProcessingException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URLEncoder;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Dynamic Proxy Invocation Handler
 * @author zhe.yangz 2011-12-28 下午04:47:56
 */
public class ProxyInvocationHandler implements InvocationHandler {
    
    private static final String TAG = "ProxyInvocationHandler";

    public Object bind(Class<?> clazz) {
        Class<?>[] clazzs = {clazz};
        Object newProxyInstance = Proxy.newProxyInstance(
                clazz.getClassLoader(), 
                clazzs, 
                this);
        return newProxyInstance;
    }
    
    /** 
     * Dynamic proxy invoke
     */ 
    public Object invoke(Object proxy, Method method, Object[] args)  
            throws Throwable {
        AkPOST akPost = method.getAnnotation(AkPOST.class);
        AkGET akGet = method.getAnnotation(AkGET.class);
        
        AkAPI akApi = method.getAnnotation(AkAPI.class);
        Annotation[][] annosArr = method.getParameterAnnotations();
        String invokeUrl = akApi.url();
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

        // AkApiParams to hashmap, filter out of null-value
        HashMap<String, File> filesToSend = new HashMap<String, File>();
        HashMap<String, String> paramsMapOri = new HashMap<String, String>();
        HashMap<String, String> paramsMap =
                getRawApiParams2HashMap(annosArr, args, filesToSend, paramsMapOri);
        // Record this invocation
        ApiInvokeInfo apiInvokeInfo = new ApiInvokeInfo();
        apiInvokeInfo.apiName = method.getName();
        apiInvokeInfo.paramsMap.putAll(paramsMapOri);
        apiInvokeInfo.url = invokeUrl;
        // parse '{}'s in url
        invokeUrl = parseUrlbyParams(invokeUrl, paramsMap);
        // cleared hashmap to params, and filter out of the null value
        Iterator<Entry<String, String>> iter = paramsMap.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, String> entry = iter.next();
            params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        
        // get the signature string if using
        AkSignature akSig = method.getAnnotation(AkSignature.class);
        if (akSig != null) {
            Class<?> clazzSignature = akSig.using();
            if (clazzSignature.getInterfaces().length > 0 // TODO: NEED VERIFY WHEN I HAVE TIME
                    && InvokeSignature.class.getName().equals(
                            clazzSignature.getInterfaces()[0].getName())) {
                InvokeSignature is =
                    (InvokeSignature) clazzSignature.getConstructors()[0].newInstance();
                String sigValue = is.signature(akSig, invokeUrl, params, paramsMapOri);
                String sigParamName = is.getSignatureParamName();
                if (sigValue != null && sigParamName != null
                        && sigValue.length()>0 && sigParamName.length()>0 ) {
                    params.add(new BasicNameValuePair(sigParamName, sigValue));
                } 
            }
        }
        
        // choose POST GET PUT DELETE to use for this invoke
        String retString = "";
        if (akGet != null) {
            StringBuilder sbUrl = new StringBuilder(invokeUrl);
            if (!(invokeUrl.endsWith("?") || invokeUrl.endsWith("&"))) {
                sbUrl.append("?");
            }
            for (NameValuePair nvp : params) {
                sbUrl.append(nvp.getName());
                sbUrl.append("=");
                sbUrl.append(nvp.getValue());
                sbUrl.append("&");
            } // now default using UTF-8, maybe improved later
            retString = HttpInvoker.get(sbUrl.toString());
        } else if (akPost != null) {
            if (filesToSend.isEmpty()) {
                retString = HttpInvoker.post(invokeUrl, params);
            } else {
                retString = HttpInvoker.postWithFilesUsingURLConnection(
                        invokeUrl, params, filesToSend);
            }
        } else { // use POST for default
            retString = HttpInvoker.post(invokeUrl, params);
        }

        // invoked, then add to history
        //ApiStats.addApiInvocation(apiInvokeInfo);
        
        //Log.d(TAG, retString);
        
        // parse the return-string
        final Class<?> returnType = method.getReturnType();
        try {
            if (String.class.equals(returnType)) { // the result return raw string
                return retString;
            } else {                               // return object using json decode
                AkJsonPaser akJsonPaser = method.getAnnotation(AkJsonPaser.class);
                if (akJsonPaser != null && akJsonPaser.value() != null) {
                    if ("jackson".equals(akJsonPaser.value())) {
                        return JsonMapper.json2pojo(retString, returnType);
                    } else if ("gson".equals(akJsonPaser.value())) {
                        return GsonUtil.getGson().fromJson(retString, returnType);
                    } else {
                        return JsonMapper.json2pojo(retString, returnType);
                    }
                } else {
                    return JsonMapper.json2pojo(retString, returnType);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, retString, e);  // log can print the error return-string
            throw new AkInvokeException(AkInvokeException.CODE_JSONPROCESS_EXCEPTION,
                    e.getMessage(), e);
        }
    }

    /**
     * Replace all the {} block in url to the actual params, 
     * clear the params used in {block}, return cleared params HashMap and replaced url.
     * @param url such as http://server/{namespace}/1/do
     * @param params such as hashmap include (namespace->'mobile')
     * @return the parsed param will be removed in HashMap (params)
     */
    private String parseUrlbyParams(String url, HashMap<String, String> params)
            throws AkInvokeException {

        StringBuffer sbUrl = new StringBuffer();
        Pattern pattern = Pattern.compile("\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(url);
        
        while (matcher.find()) {
            String paramValue = params.get(matcher.group(1));
            if (paramValue != null) {
                matcher.appendReplacement(sbUrl, paramValue);
            } else { // 对于{name}没有匹配到的则抛出异常
                throw new AkInvokeException(AkInvokeException.CODE_PARAM_IN_URL_NOT_FOUND,
                        "Parameter {"+matcher.group(1)+"}'s value not found of url "+url+".");
            }
            params.remove(matcher.group(1));
        }
        matcher.appendTail(sbUrl);
        
        return sbUrl.toString();
    }

    /**
     * AkApiParams to hashmap, filter out of null-value
     *
     * @param annosArr Method's params' annotation array[][]
     * @param args Method's params' values
     * @param filesToSend
     * @return HashMap all (paramName -> paramValue)
     */
    private HashMap<String, String> getRawApiParams2HashMap(Annotation[][] annosArr,
                                                            Object[] args,
                                                            HashMap<String, File> filesToSend,
                                                            HashMap<String, String> paramsMapOri) {
        HashMap<String, String> paramsMap = new HashMap<String, String>();
        for (int idx = 0; idx < args.length; idx++) {
            String paramName = null;
            String encode = "none";
            for (Annotation a : annosArr[idx]) {
                if(AkParam.class.equals(a.annotationType())){
                    AkParam ap =  (AkParam)a;
                    paramName = ap.value();
                    encode = ap.encode();
                }
            }
            if (paramName != null) {
                Object arg = args[idx];
                if (arg != null) { // filter out of null-value param
                    if ("$paramMap".equals(paramName)) {
                        Map<String, String> paramMap = (Map<String, String>)arg;
                        paramsMapOri.putAll(paramMap);
                        if (encode != null && !"none".equals(encode)) {
                            HashMap<String, String> encodedMap = new HashMap<String, String>();
                            for (Entry<String, String> entry : paramMap.entrySet()) {
                                try {
                                    encodedMap.put(entry.getKey(),
                                            URLEncoder.encode(entry.getValue(), encode));
                                } catch (Exception e) {
                                    Log.w(TAG, "UnsupportedEncodingException:" + encode);
                                    encodedMap.put(entry.getKey(), entry.getValue());
                                }
                            }
                            paramsMap.putAll(encodedMap);
                        } else {
                            paramsMap.putAll(paramMap);
                        }
                    } else if ("$filesToSend".equals(paramName)) {
                        if (arg instanceof Map) {
                            Map<String, File> files = (Map<String, File>)arg;
                            filesToSend.putAll(files);
                        }
                    } else if (encode != null && !"none".equals(encode)) {
                        try {
                            paramsMap.put(paramName, URLEncoder.encode(arg.toString(), encode));
                        } catch (UnsupportedEncodingException e) {
                            Log.w(TAG, "UnsupportedEncodingException:" + encode);
                            paramsMap.put(paramName, arg.toString());
                        }
                        paramsMapOri.put(paramName, arg.toString());
                    } else {
                        paramsMap.put(paramName, arg.toString());
                        paramsMapOri.put(paramName, arg.toString());
                    }
                }
            }
        }
        return paramsMap;
    }    
}  
