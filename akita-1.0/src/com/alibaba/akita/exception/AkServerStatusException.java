/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.akita.exception;

import java.io.IOException;

import com.alibaba.akita.util.JsonMapper;
import com.alibaba.akita.util.Log;
import org.codehaus.jackson.JsonParseException;



/**
 * server error such as error(500) or exception(401 403) & etc
 * @author zhe.yangz 2012-1-17 下午06:57:53
 */
public class AkServerStatusException extends AkException{
    private static final long serialVersionUID = 8831634121316777078L;

    private static final String TAG = "AkServerStatusException";

    /**
     * exception code
     */
    public int code;
    
    /**
     * get the server error detail
     * @param <T>
     * @param clazz
     * @return null if exception, otherwise errorMsg T
     * @throws IOException 
     * @throws JsonParseException
     */
    public <T> T getServerError(Class<T> clazz) {
        try {
            return JsonMapper.json2pojo(getMessage(), clazz);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "parse error:"+getMessage());
            return null;
        }
    }

    @SuppressWarnings("unused")
    private AkServerStatusException(){
        super();
    }
    @SuppressWarnings("unused")
    private AkServerStatusException(Throwable t){
        super(t);
    }
    
    public AkServerStatusException(int code, String msg){
        super(msg);
        
        this.code = code;
    }
    
    public AkServerStatusException(int code, String msg, Throwable t){
        super(msg,t);
        
        this.code = code;
    }
    


}
