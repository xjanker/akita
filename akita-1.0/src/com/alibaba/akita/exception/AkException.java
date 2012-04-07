/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.akita.exception;

/**
 * the root exception of Akita lib. 
 * @author zhe.yangz 2012-1-17 下午06:57:53
 */
public class AkException extends Exception {
    private static final long serialVersionUID = -2431196726844826744L;
    
    protected AkException(){
        super();
    }

    protected AkException(Throwable t){
        super(t);
    }
    
    public AkException(String msg){
        super(msg);
    }
    
    public AkException(String msg, Throwable t){
        super(msg,t);
    }
}
