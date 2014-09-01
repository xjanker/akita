/*
 * Copyright 2011 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package org.akita.util;


/**
 * 封装Log 
 * @author zhe.yangz 2011-11-25 下午04:06:57
 */
public class Log {
    static final boolean SHOW_LOG = true;

    public static void i(String tag, String string) {
        if (SHOW_LOG) android.util.Log.i(tag, string);
    }
    public static void e(String tag, String string) {
        if (SHOW_LOG) android.util.Log.e(tag, string);
    }
    public static void e(String tag, String string, Throwable tr) {
        if (SHOW_LOG) android.util.Log.e(tag, string, tr);
    }
    public static void d(String tag, String string) {
        if (SHOW_LOG) android.util.Log.d(tag, string);
    }
    public static void w(String tag, String string) {
        if (SHOW_LOG) android.util.Log.w(tag, string);
    }
    public static void w(String tag, String string, Throwable tr) {
        if (SHOW_LOG) android.util.Log.w(tag, string, tr);
    }
    public static void v(String tag, String string) {
        if (SHOW_LOG) android.util.Log.v(tag, string);
    }
    public static void v(String tag, String string, Throwable tr) {
        if (SHOW_LOG) android.util.Log.v(tag, string, tr);
    }

}

