/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.akita.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.codehaus.jackson.*;
import org.codehaus.jackson.JsonParser.Feature;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * GSON Util
 *
 * @author zhe.yangz
 */
public class GsonUtil {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Gson cachedGson = null;

    public static Gson getGson() {
        if (cachedGson == null) {
            cachedGson = new GsonBuilder().setDateFormat(DATE_FORMAT).create();
        }
        return cachedGson;
    }
    
}
