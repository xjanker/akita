/*
 * Copyright 2011 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.akita.proxy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define that using GET Http/Https to invoke 
 * @author zhe.yangz 2011-12-29 下午06:45:16
 */
@Retention(RetentionPolicy.RUNTIME)   
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AkGET {
    /**
     * Use GET
     * @return
     */
    public boolean value() default true;
}
