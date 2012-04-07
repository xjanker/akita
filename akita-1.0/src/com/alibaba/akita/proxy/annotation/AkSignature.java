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
 * Define that using POST Http/Https to invoke 
 * @author zhe.yangz 2011-12-29 下午06:45:16
 */
@Retention(RetentionPolicy.RUNTIME)   
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AkSignature {
    /**
     * Use Signature
     * @return
     */
    public Class<?> using();
    /**
     * 用于验证的第一个data
     * @return
     */
    public String data0() default "";
}
